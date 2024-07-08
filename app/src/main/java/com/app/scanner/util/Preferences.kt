package com.app.scanner.util

import android.content.Context
import android.content.SharedPreferences
import com.app.scanner.util.Constants.Companion.IS_ONBOARDED
import com.app.scanner.util.Constants.Companion.IS_SWIPE_TO_DELETE_ENABLE
import com.app.scanner.util.Constants.Companion.PERMISSION_SHOWED

class Preferences {
    companion object {

        private lateinit var appPref: SharedPreferences

        fun getInstance(context: Context): SharedPreferences {
            if (!Companion::appPref.isInitialized) appPref =
                context.getSharedPreferences(Constants.DATA, Context.MODE_PRIVATE)
            return appPref
        }

        fun getOnboarded(): Boolean {
            return appPref.getBoolean(IS_ONBOARDED, false)
        }

        fun setOnboarded(isOnboarded: Boolean) {
            appPref.edit().putBoolean(IS_ONBOARDED, isOnboarded).apply()
        }

        fun getPermissionShowed(): Long {
            return appPref.getLong(PERMISSION_SHOWED, 0)
        }

        fun setPermissionShowed(permissionShowed: Long) {
            appPref.edit().putLong(PERMISSION_SHOWED, permissionShowed).apply()
        }

        fun setIsSwipeToDeleteEnable(isSwipeToDeleteEnable: Boolean) {
            appPref.edit().putBoolean(IS_SWIPE_TO_DELETE_ENABLE, isSwipeToDeleteEnable).apply()
        }

        fun getIsSwipeToDeleteEnable(): Boolean {
            return appPref.getBoolean(IS_SWIPE_TO_DELETE_ENABLE, false)
        }
    }
}
