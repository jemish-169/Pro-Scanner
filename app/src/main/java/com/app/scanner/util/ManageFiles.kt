package com.app.scanner.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

fun checkAndCreateDirectory(): File {
    val dirPath: String =
        Environment.getExternalStorageDirectory().absolutePath + File.separator + "Pro Scanner"
    val projDir = File(dirPath)
    if (!projDir.exists())
        projDir.mkdirs()

    return projDir
}

fun checkAndCreateInternalDirectory(context: Activity): File {
    val dirPath: String = context.filesDir.absolutePath + File.separator + "Pro Scanner"
    val projDir = File(dirPath)
    if (!projDir.exists())
        projDir.mkdirs()
    return projDir
}

fun saveFileInDirectory(directory: File, fileName: String, fileContent: File) {
    val file = File(directory, fileName)
    try {
        FileInputStream(fileContent).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun getListFiles(parentDir: String): List<Uri> {
    val f = File(parentDir)
    val filesUri = arrayListOf<Uri>()
    val files = f.listFiles()
    if (files != null && files.isNotEmpty())
        for (i in files) {
            filesUri.add(i.toUri())
        }
    return filesUri
}

fun shareSelectedFiles(context: Activity, fileList: List<Uri>) {
    if (fileList.isEmpty()) return

    val intent = Intent().apply {
        action = Intent.ACTION_SEND_MULTIPLE
        type = "application/pdf"

        val urisToShare = fileList.mapNotNull { uri ->
            when (uri.scheme) {
                "content" -> uri
                "file" -> {
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        File(uri.path!!)
                    )
                }

                else -> null
            }
        }

        putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(urisToShare))
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val chooserIntent = Intent.createChooser(intent, "Share files")
    context.startActivity(chooserIntent)
}

fun deleteGivenFiles(context: Activity, uriList: List<Uri>): List<Uri> {
    val failedDeletions = mutableListOf<Uri>()

    for (uri in uriList) {
        val isDeleted = when {
            uri.scheme == "file" -> deleteFileFromPath(uri.path)
            uri.scheme == "content" -> deleteFileFromContentUri(context, uri)
            else -> false
        }

        if (!isDeleted) {
            failedDeletions.add(uri)
        }
    }

    return failedDeletions
}

private fun deleteFileFromPath(path: String?): Boolean {
    if (path == null) return false
    val file = File(path)
    return file.exists() && file.delete()
}

private fun deleteFileFromContentUri(context: Activity, uri: Uri): Boolean {
    val documentFile = DocumentFile.fromSingleUri(context, uri)
    return documentFile?.exists() == true && documentFile.delete()
}