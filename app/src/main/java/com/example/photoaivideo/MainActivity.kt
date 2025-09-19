
package com.example.photoaivideo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import fi.iki.elonen.NanoHTTPD
import java.io.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    lateinit var webView: WebView
    var server: LocalServer? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // permissions (READ external storage for picking files)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1001)
            }
        }

        webView = WebView(this)
        setContentView(webView)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        // start server
        coroutineScope.launch {
            server = LocalServer(8080, assets, cacheDir)
            server?.start()
            withContext(Dispatchers.Main) {
                webView.loadUrl("http://127.0.0.1:8080/")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        server?.stop()
    }
}

// LocalServer: handles static assets and a /api/generate endpoint that accepts multipart form uploads
class LocalServer(port: Int, val assetManager: android.content.res.AssetManager, val cacheDir: File) : NanoHTTPD(port) {
    val client = OkHttpClient.Builder().build()
    val TAG = "LocalServer"

    override fun serve(session: IHTTPSession): Response {
        try {
            val uri = session.uri
            if (uri == "/") {
                return serveAsset("www/index.html")
            } else if (uri.startsWith("/api/generate") && session.method == Method.POST) {
                return handleGenerate(session)
            } else if (uri.startsWith("/www/") || uri.startsWith("/assets/")) {
                val path = uri.removePrefix("/")
                return serveAsset(path)
            } else {
                return serveAsset("www" + uri)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Serve error", e)
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "Server error: ${e.message}")
        }
    }

    fun serveAsset(path: String): Response {
        try {
            val input = assetManager.open(path)
            val bytes = input.readBytes()
            input.close()
            val mime = when {
                path.endsWith(".html") -> "text/html"
                path.endsWith(".js") -> "application/javascript"
                path.endsWith(".css") -> "text/css"
                path.endsWith(".png") -> "image/png"
                else -> "application/octet-stream"
            }
            return newFixedLengthResponse(Response.Status.OK, mime, ByteArrayInputStream(bytes), bytes.size.toLong())
        } catch (e: Exception) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not found")
        }
    }

    // Handle /api/generate - robust implementation sketch:
    // - parse multipart form-data (NanoHTTPD can populate session.parms and session.files via parseBody)
    // - expected fields: photos (multiple file parts), optional reference (photo or video), duration, style, useAI
    fun handleGenerate(session: IHTTPSession): Response {
        try {
            val files = HashMap<String, String>()
            val params = HashMap<String, String>()
            session.parseBody(files)
            params.putAll(session.parms)

            // Collect uploaded photos
            val photoPaths = mutableListOf<String>()
            for ((k,v) in files) {
                if (k.startsWith("photo") || k.startsWith("photos") || k.startsWith("photos[]") || k.startsWith("photos[")) {
                    photoPaths.add(v)
                }
            }
            // Fallback: NanoHTTPD may store files with field names; collect all temp files
            if (photoPaths.isEmpty()) {
                for ((k,v) in files) {
                    if (v.endsWith(".tmp") || v.endsWith(".tmpfile")) {
                        // naive - include but ensure they are images
                        photoPaths.add(v)
                    }
                }
            }

            if (photoPaths.isEmpty()) {
                // check for files in temp mapped by other keys
                // return error
                val json = JSONObject()
                json.put("status","error")
                json.put("message","No photos uploaded. Ensure multipart field is named 'photos' or 'photo'.")
                return newFixedLengthResponse(Response.Status.BAD_REQUEST, "application/json", json.toString())
            }

            val duration = params["duration"]?.toIntOrNull() ?: 8
            val style = params["style"] ?: "smooth"
            val useAI = params["useAI"] == "true" || params["useAI"] == "1"

            // Save files into an app-specific temp folder
            val jobDir = File(cacheDir, "job_" + System.currentTimeMillis().toString())
            if (!jobDir.exists()) jobDir.mkdirs()

            val savedPhotos = mutableListOf<File>()
            for ((i, tmpPath) in photoPaths.withIndex()) {
                val src = File(tmpPath)
                val dest = File(jobDir, "input_${i}${getExtForFile(src)}")
                src.copyTo(dest, overwrite = true)
                savedPhotos.add(dest)
            }

            // find reference if present in params files map
            var refFile: File? = null
            for ((k,v) in files) {
                if (k.contains("reference") || k.contains("ref")) {
                    val f = File(v)
                    val dest = File(jobDir, "reference${getExtForFile(f)}")
                    f.copyTo(dest, overwrite = true)
                    refFile = dest
                }
            }

            // Process: generate frames from photos using FFmpeg zoompan or scale/extract
            // We'll create frames per photo and then optionally call Replicate for AI enhancement
            val totalFrames = Math.max(30, (30.0 * (duration / 3.0)).toInt())
            val framesPerPhoto = Math.max(6, totalFrames / savedPhotos.size)

            // Simple motion curve heuristic based on reference (if video) - else default subtle zoom
            val motionCurve = if (refFile != null && isVideoFile(refFile)) {
                // extract duration and compute simple pan/zoom values
                computeMotionFromVideo(refFile, framesPerPhoto)
            } else {
                generateDefaultMotion(framesPerPhoto)
            }

            // generate frames for each photo
            val timelineDir = File(jobDir, "frames")
            timelineDir.mkdirs()
            var frameIndex = 0
            for ((idx, photo) in savedPhotos.withIndex()) {
                val outDir = File(jobDir, "photo_${idx}")
                outDir.mkdirs()
                generateFramesForPhoto(photo, outDir, framesPerPhoto, motionCurve, refFile, useAI)
                // copy frames to timeline
                val filesSorted = outDir.listFiles { f -> f.name.endsWith(".png") }?.sortedBy { it.name } ?: listOf()
                for (f in filesSorted) {
                    val dst = File(timelineDir, String.format("frame_%06d.png", frameIndex))
                    f.copyTo(dst, overwrite = true)
                    frameIndex++
                }
                // crossfade duplicates (simple)
                for (k in 0 until 6) {
                    val last = filesSorted.lastOrNull()
                    if (last != null) {
                        val dst = File(timelineDir, String.format("frame_%06d.png", frameIndex))
                        last.copyTo(dst, overwrite = true)
                        frameIndex++
                    }
                }
            }

//            // encode frames into mp4 using FFmpegKit
            val outVideo = File(jobDir, "out.mp4")
            val fps = Math.max(15, Math.round((savedPhotos.size * framesPerPhoto).toFloat() / duration).toInt())
            val ffmpegCmd = "-y -framerate $fps -i ${timelineDir.absolutePath}/frame_%06d.png -c:v libx264 -pix_fmt yuv420p ${outVideo.absolutePath}"
//            val sessionFF = FFmpegKit.execute(ffmpegCmd)
            val returnCode = sessionFF.returnCode
//            if (!ReturnCode.isSuccess(returnCode)) {
                Log.e(TAG, "FFmpeg encode failed: " + sessionFF.failStackTrace)
                val json = JSONObject()
                json.put("status","error")
                json.put("message","FFmpeg encode failed.")
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "application/json", json.toString())
            }

            // Return the MP4 binary as response
            val bytes = outVideo.readBytes()
            val resp = newFixedLengthResponse(Response.Status.OK, "video/mp4", ByteArrayInputStream(bytes), bytes.size.toLong())
            resp.addHeader("Content-Disposition", "attachment; filename=\"out.mp4\"")
            return resp

        } catch (e: Exception) {
            Log.e("LocalServer", "generate error", e)
            val json = JSONObject()
            json.put("status","error")
            json.put("message", e.message)
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "application/json", json.toString())
        }
    }

    // Helpers

    fun getExtForFile(f: File): String {
        val name = f.name
        val i = name.lastIndexOf('.')
        return if (i >= 0) name.substring(i) else ".bin"
    }

    fun isVideoFile(f: File): Boolean {
        val ext = f.extension.toLowerCase()
        return ext == "mp4" || ext == "mov" || ext == "webm" || ext == "mkv"
    }

    fun computeMotionFromVideo(videoFile: File, framesNeeded: Int): List<MotionPoint> {
//        // Simple heuristic: use ffprobe via FFmpegKit to get duration, then craft a sine-based pan/zoom
        val motions = mutableListOf<MotionPoint>()
        try {
            val probeCmd = "-v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 \"${videoFile.absolutePath}\""
//            val probe = FFmpegKit.execute(probeCmd)
            val out = probe.output ?: ""
            val dur = out.trim().toDoubleOrNull() ?: 2.0
            val panX = 0.06 * (Math.random() - 0.5)
            val panY = 0.04 * (Math.random() - 0.5)
            val zoom = 1.06 + Math.random() * 0.08
            for (i in 0 until framesNeeded) {
                val t = i.toDouble() / Math.max(1, framesNeeded - 1)
                val z = 1.0 + (zoom - 1.0) * (Math.sin(t * Math.PI - Math.PI/2) * 0.5 + 0.5)
                motions.add(MotionPoint(panX * (t*2 -1), panY * (t*2 -1), z))
            }
        } catch (e: Exception) {
            for (i in 0 until framesNeeded) motions.add(MotionPoint(0.0,0.0,1.0 + 0.02 * i))
        }
        return motions
    }

    fun generateDefaultMotion(framesNeeded: Int): List<MotionPoint> {
        val motions = mutableListOf<MotionPoint>()
        for (i in 0 until framesNeeded) {
            val t = i.toDouble() / Math.max(1, framesNeeded - 1)
            motions.add(MotionPoint(0.0, 0.0, 1.0 + 0.02 * t))
        }
        return motions
    }

    fun generateFramesForPhoto(photo: File, outDir: File, framesPerImage: Int, motionCurve: List<MotionPoint>, refFile: File?, useAI: Boolean) {
        // We'll use FFmpeg's zoompan filter to generate frames from a single image.
        // Example zoompan: -loop 1 -i photo.png -vf \"zoompan=z='zoom+0.001':d=25\" -frames:v N out%03d.png
        // We'll iterate and produce PNG frames.
        for (i in 0 until framesPerImage) {
            val mp = motionCurve.getOrElse(i) { MotionPoint(0.0,0.0,1.0) }
            // Create a temporary short video applying zoom to capture a single frame, then extract frame as image.
            val tmpVid = File(outDir, "tmp_${i}.mp4")
            val outPng = File(outDir, String.format("frame_%04d.png", i))
            val zoomExpr = mp.zoom
            // using simple scale and crop approach: scale larger then crop center using ffmpeg crop filter
            val cmd = "-y -loop 1 -i \"${photo.absolutePath}\" -vf \"scale=iw*${zoomExpr}:ih*${zoomExpr},crop=iw:ih\" -frames:v 1 \"${outPng.absolutePath}\""
//            val session = FFmpegKit.execute(cmd)
            val rc = session.returnCode
//            if (!ReturnCode.isSuccess(rc)) {
                // fallback: copy original image to outPng
                try { photo.copyTo(outPng, overwrite = true) } catch(e:Exception){}
            } else {
                // optionally call Replicate to enhance frame
                if (useAI) {
                    try {
                        val enhanced = callReplicateEnhance(outPng)
                        if (enhanced != null) {
                            // enhanced is byte array, overwrite outPng
                            val fos = FileOutputStream(outPng)
                            fos.write(enhanced)
                            fos.close()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Replicate call failed", e)
                    }
                }
            }
        }
    }

    // Call Replicate API to enhance a single frame. Returns byte[] of image or null.
    fun callReplicateEnhance(pngFile: File): ByteArray? {
        try {
            val token = System.getenv("REPLICATE_API_TOKEN") ?: "" // set via environment or other secure store
            if (token.isBlank()) return null
            val modelVersion = "" // TODO: set the Replicate model version or id you want to use
            // Read file to base64
            val bytes = pngFile.readBytes()
            val b64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
            // Build JSON according to the model's input schema. This is a placeholder.
            val json = JSONObject()
            json.put("version", modelVersion)
            val input = JSONObject()
            input.put("image", "data:image/png;base64,$b64")
            input.put("motion_style", "replicate_motion")
            json.put("input", input)
            val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString())
            val req = Request.Builder()
                .url("https://api.replicate.com/v1/predictions")
                .addHeader("Authorization", "Token $token")
                .post(body)
                .build()
            val resp = client.newCall(req).execute()
            if (!resp.isSuccessful) return null
            val respBody = resp.body?.string() ?: return null
            val job = JSONObject(respBody)
            // The Replicate API returns an output URL after processing; for simplicity poll until finished.
            val id = job.optString("id", "")
            if (id.isBlank()) return null
            // Poll prediction result
            var resultUrl: String? = null
            for (i in 0 until 40) {
                Thread.sleep(1000)
                val statusReq = Request.Builder()
                    .url("https://api.replicate.com/v1/predictions/$id")
                    .addHeader("Authorization", "Token $token")
                    .get()
                    .build()
                val statusResp = client.newCall(statusReq).execute()
                if (!statusResp.isSuccessful) continue
                val statusBody = statusResp.body?.string() ?: continue
                val statusJson = JSONObject(statusBody)
                val st = statusJson.optString("status", "")
                if (st == "succeeded") {
                    val output = statusJson.optJSONArray("output")
                    if (output != null && output.length() > 0) {
                        resultUrl = output.getString(0)
                    }
                    break
                } else if (st == "failed") {
                    break
                }
            }
            if (resultUrl != null) {
                // fetch result image
                val r = Request.Builder().url(resultUrl).get().build()
                val rresp = client.newCall(r).execute()
                if (!rresp.isSuccessful) return null
                val bytesOut = rresp.body?.bytes()
                return bytesOut
            }
            return null
        } catch (e: Exception) {
            Log.e("LocalServer", "replicate error", e)
            return null
        }
    }
}

data class MotionPoint(val panX: Double, val panY: Double, val zoom: Double)
