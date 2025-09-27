package com.example.photoaivideo

import java.io.File

object FileUtils {
    fun moveToRecycleBin(file: File, recycleBinDir: File): Boolean {
        if (!recycleBinDir.exists()) recycleBinDir.mkdirs()
        val target = File(recycleBinDir, file.name)
        return file.renameTo(target)
    }
}
