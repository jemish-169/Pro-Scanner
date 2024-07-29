package com.app.scanner.util

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.text.format.DateUtils
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun checkPermission(context: Activity): Boolean {
    val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        (Environment.isExternalStorageManager())
    } else {
        (ContextCompat.checkSelfPermission(
            context, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }
    return result
}

fun getVersionName(context: Activity): String {
    var versionName = ""
    try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        versionName = packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return versionName
}

fun formatMillisToDate(millis: Long): String {
    val calendar = Calendar.getInstance()

    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    calendar.add(Calendar.DAY_OF_YEAR, -1)
    val yesterday = calendar.timeInMillis

    return when {
        DateUtils.isToday(millis) -> "Today"
        millis > yesterday -> "Yesterday"
        else -> {
            val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            sdf.format(Date(millis))
        }
    }
}

fun getTodayDate(): String {
    val currentTime = System.currentTimeMillis()
    val sdf = SimpleDateFormat("dd_MM_", Locale.getDefault())
    return sdf.format(currentTime) + currentTime.toString().takeLast(8)
}

fun showPermissionDialogFrequency(context: Activity): Boolean {
    if (!DateUtils.isToday(Preferences.getPermissionShowed()) && !checkPermission(context)) {
        Preferences.setPermissionShowed(System.currentTimeMillis())
        return true
    }
    return false
}

fun askPermission(context: Activity): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (!Environment.isExternalStorageManager()) {
            val uri = Uri.parse("package:${context.packageName}")
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
            context.startActivity(intent)
        } else return true
    } else {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1001
            )
        } else return true
    }
    return false
}

fun scanDoc(
    context: Activity,
    scannerLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>
) {
    val options = GmsDocumentScannerOptions.Builder().setScannerMode(SCANNER_MODE_FULL)
        .setGalleryImportAllowed(true).setResultFormats(RESULT_FORMAT_JPEG, RESULT_FORMAT_PDF)
        .build()
    val scanner = GmsDocumentScanning.getClient(options)
    scanner.getStartScanIntent(context).addOnSuccessListener {
        scannerLauncher.launch(
            IntentSenderRequest.Builder(it).build()
        )
    }.addOnFailureListener {
        Toast.makeText(
            context, "Something went wrong!", Toast.LENGTH_SHORT
        ).show()
    }
}