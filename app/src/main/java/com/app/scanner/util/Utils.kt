package com.app.scanner.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.text.format.DateUtils
import androidx.core.content.ContextCompat
import java.util.Calendar

fun checkPermission(context: Activity): Boolean {
    val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        (Environment.isExternalStorageManager())
    } else {
        (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }
    return result
}

fun getVersionName(context: Activity): String {
    var versionName = ""
    try {
        val packageInfo =
            context.packageManager.getPackageInfo(context.packageName, 0)
        versionName = packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return versionName
}

fun showPermissionDialogFrequency(context: Activity): Boolean {
    if (!DateUtils.isToday(Preferences.getPermissionShowed()) && !checkPermission(context)) {
        Preferences.setPermissionShowed(Calendar.getInstance().timeInMillis)
        return true
    }
    return false
}