package com.app.scanner.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.text.format.DateUtils
import androidx.core.content.ContextCompat
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
    val currentTime = Calendar.getInstance().timeInMillis
    val sdf = SimpleDateFormat("dd_MM_", Locale.getDefault())
    return sdf.format(currentTime) + currentTime.toString().drop(7)
}

fun showPermissionDialogFrequency(context: Activity): Boolean {
    if (!DateUtils.isToday(Preferences.getPermissionShowed()) && !checkPermission(context)) {
        Preferences.setPermissionShowed(Calendar.getInstance().timeInMillis)
        return true
    }
    return false
}