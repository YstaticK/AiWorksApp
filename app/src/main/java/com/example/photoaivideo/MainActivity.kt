package com.example.photoaivideo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import fi.iki.elonen.NanoHTTPD
import java.io.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    lateinit var webView: WebView
    var server: LocalServer? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1001
                )
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

// LocalServer
class LocalServer(
    port: Int,
    val assetManager: android.content.res.AssetManager,
    val cacheDir: File
) : NanoHTTPD(port) {
    val client = OkHttpClient.Builder().build()
    val TAG = "LocalServer"

    override fun serve(session: IHTTPSession): Response {
        try {
            val uri = session.uri
            return when {
                uri == "/" -> serveAsset("www/index.html")
                uri.startsWith("/api/generate") && session.method == Method.POST -> handleGenerate(
                    session
                )
                uri.startsWith("/www/") || uri.startsWith("/assets/") -> {
                    val path = uri.removePrefix("/")
                    serveAsset(path)
                }
                else -> serveAsset("www$uri")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Serve error", e)
            return newFixedLengthResponse(
                Response.Status.INTERNAL_ERROR,
                "text/plain",
                "Server error: ${e.message}"
            )
        }
    }

    fun serveAsset(path: String): Response {
        return try {
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
            newFixedLengthResponse(
                Response.Status.OK,
                mime,
                ByteArrayInputStream(bytes),
                bytes.size.toLong()
            )
        } catch (e: Exception) {
            newFixedLengthResponse(
                Response.Status.NOT_FOUND,
                "text/plain",
                "Not found"
            )
        }
    }

    fun handleGenerate(session: IHTTPSession): Response {
        try {
            val files = HashMap<String, String>()
            val params = HashMap<String, String>()
            session.parseBody(files)
            params.putAll(session.parms)

            val photoPaths = mutableListOf<String>()
            for ((k, v) in files) {
                if (k.startsWith("photo") || k.startsWith("photos")) {
                    photoPaths.add(v)
                }
            }
            if (photoPaths.isEmpty()) {
                val json = JSONObject()
                json.put("status", "error")
                json.put(
                    "message",
                    "No photos uploaded. Ensure multipart field is named 'photos' or 'photo'."
                )
                return newFixedLengthResponse(
                    Response.Status.BAD_REQUEST,
                    "application/json",
                    json.toString()
                )
            }

            val duration = params["duration"]?.toIntOrNull() ?: 8
            val useAI = params["useAI"] == "true" || params["useAI"] == "1"

            val jobDir = File(cacheDir, "job_" + System.currentTimeMillis())
            if (!jobDir.exists()) jobDir.mkdirs()

            val savedPhotos = mutableListOf<File>()
            for ((i, tmpPath) in photoPaths.withIndex()) {
                val src = File(tmpPath)
                val dest = File(jobDir, "input_${i}${getExtForFile(src)}")
                src.copyTo(dest, overwrite = true)
                savedPhotos.add(dest)
            }

            // Stub: instead of FFmpeg, just copy first photo as video placeholder
            val outVideo = File(jobDir, "out.mp4")
            if (savedPhotos.isNotEmpty()) {
                savedPhotos.first().copyTo(outVideo, overwrite = true)
            }

            val bytes = outVideo.readBytes()
            val resp = newFixedLengthResponse(
                Response.Status.OK,
                "video/mp4",
                ByteArrayInputStream(bytes),
                bytes.size.toLong()
            )
            resp.addHeader("Content-Disposition", "attachment; filename=\"out.mp4\"")
            return resp
        } catch (e: Exception) {
            Log.e("LocalServer", "generate error", e)
            val json = JSONObject()
            json.put("status", "error")
            json.put("message", e.message)
            return newFixedLengthResponse(
                Response.Status.INTERNAL_ERROR,
                "application/json",
                json.toString()
            )
        }
    }

    fun getExtForFile(f: File): String {
        val name = f.name
        val i = name.lastIndexOf('.')
        return if (i >= 0) name.substring(i) else ".bin"
    }
}

data class MotionPoint(val panX: Double, val panY: Double, val zoom: Double)
