package com.app.scanner.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale

fun checkAndCreateExternalParentDir(): File {
    val dirPath: String =
        Environment.getExternalStorageDirectory().absolutePath + File.separator + "Pro Scanner"
    val projDir = File(dirPath)
    if (!projDir.exists())
        projDir.mkdirs()

    return projDir
}

fun checkAndCreateInternalParentDir(context: Activity): File {
    val dirPath: String = context.filesDir.absolutePath + File.separator + "Pro Scanner"
    val projDir = File(dirPath)
    if (!projDir.exists())
        projDir.mkdirs()
    return projDir
}

fun checkAndCreateChildDir(directory: File, category: String): File {
    val dirPath: String = directory.absolutePath + File.separator + category
    val projDir = File(dirPath)
    if (!projDir.exists())
        projDir.mkdirs()
    return projDir
}

fun saveFileInDirectory(
    directory: File,
    fileName: String,
    fileContent: File,
    category: String
): Uri {
    val projDir = checkAndCreateChildDir(directory, category)
    val file = File(projDir, "$fileName.pdf")
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
    return file.toUri()
}

fun renameAndMoveFile(directory: File, fileName: String, fileContent: File, category: String): Uri {
    val projDir = checkAndCreateChildDir(directory, category)
    val to = File(projDir, fileName)
    if (projDir.exists() && fileContent.exists()) fileContent.renameTo(to)
    return to.toUri()
}

fun getListFiles(parentDir: File): List<Pair<Uri, String>> {
    val pdfFilesUri = mutableListOf<Pair<Uri, String>>()
    listPdfFilesRecursive(parentDir, pdfFilesUri)
    return pdfFilesUri
}

private fun listPdfFilesRecursive(directory: File, pdfFilesUri: MutableList<Pair<Uri, String>>) {
    val files = directory.listFiles()
    if (files != null && files.isNotEmpty()) {
        for (file in files) {
            if (file.isDirectory) {
                listPdfFilesRecursive(file, pdfFilesUri)
            } else if (file.extension.equals("pdf", ignoreCase = true)) {
                pdfFilesUri.add(Pair(file.toUri(), directory.name))
            }
        }
    }
}

fun shareSelectedFiles(context: Activity, fileList: List<Pair<Uri, String>>) {
    val newList = fileList.map { it.first }
    if (newList.isEmpty()) return

    val intent = Intent().apply {
        action = Intent.ACTION_SEND_MULTIPLE
        type = "application/pdf"

        val urisToShare = newList.mapNotNull { uri ->
            when (uri.scheme) {
                "content" -> uri
                "file" -> {
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.file_provider",
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

fun saveFileToSelectedLocation(context: Context, destinationUri: Uri, originalFile: Uri) {
    val contentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(originalFile)
    val outputStream = contentResolver.openOutputStream(destinationUri)
    try {
        inputStream?.use { input ->
            outputStream?.use { output ->
                input.copyTo(output)
            }
        }
        Toast.makeText(context, "File copied successfully", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Something went wrong, File not saved!", Toast.LENGTH_SHORT).show()

    } finally {
        inputStream?.close()
        outputStream?.close()
    }
}


fun deleteGivenFiles(context: Activity, uriList: List<Pair<Uri, String>>): List<Uri> {
    val failedDeletions = mutableListOf<Uri>()

    for (uri in uriList) {
        val isDeleted = when (uri.first.scheme) {
            "file" -> deleteFileFromPath(uri.first.path)
            "content" -> deleteFileFromContentUri(context, uri.first)
            else -> false
        }

        if (!isDeleted) {
            failedDeletions.add(uri.first)
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

fun formatFileSize(sizeInBytes: Long): String {
    val kiloByte = 1024
    val megaByte = kiloByte * 1024
    val gigaByte = megaByte * 1024

    return when {
        sizeInBytes >= gigaByte -> {
            val sizeInGB = sizeInBytes.toDouble() / gigaByte
            String.format(Locale.getDefault(), "%.2f GB", sizeInGB)
        }

        sizeInBytes >= megaByte -> {
            val sizeInMB = sizeInBytes.toDouble() / megaByte
            String.format(Locale.getDefault(), "%.2f MB", sizeInMB)
        }

        sizeInBytes >= kiloByte -> {
            val sizeInKB = sizeInBytes.toDouble() / kiloByte
            String.format(Locale.getDefault(), "%.2f KB", sizeInKB)
        }

        else -> {
            String.format(Locale.getDefault(), "%d Bytes", sizeInBytes)
        }
    }
}
