package com.app.scanner.repository

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.app.scanner.R
import com.app.scanner.util.Constants.Companion.THEME_NAME
import com.app.scanner.util.Constants.Companion.THEME_PREFERENCES
import com.app.scanner.util.ThemeOption
import com.app.scanner.util.checkAndCreateExternalParentDir
import com.app.scanner.util.checkAndCreateInternalParentDir
import com.app.scanner.util.getListFiles
import com.app.scanner.util.savePdfPagesAsImages
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

@Singleton
class Repository(private val context: Activity) {

    private val _docList = mutableListOf<Pair<Uri, String>>()

    private val Context.dataStore by preferencesDataStore(THEME_PREFERENCES)

    companion object {
        val THEME_KEY = intPreferencesKey(THEME_NAME)
    }

    suspend fun fetchData(): List<Pair<Uri, String>> {
        coroutineScope {
            checkAndCreateExternalParentDir().let { directory ->
                _docList.addAll(getListFiles(directory))
            }
        }
        coroutineScope {
            checkAndCreateInternalParentDir(context).let { directory ->
                _docList.addAll(getListFiles(directory))
            }
        }
        return _docList
    }

    fun getTheme(): Flow<ThemeOption> {
        return context.dataStore.data.map { preferences ->
            val theme =
                if (preferences[THEME_KEY] != null) {
                    preferences[THEME_KEY]
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    ThemeOption.DYNAMIC.ordinal
                } else
                    ThemeOption.SYSTEM.ordinal
            when (theme) {
                ThemeOption.LIGHT.ordinal -> ThemeOption.LIGHT
                ThemeOption.DARK.ordinal -> ThemeOption.DARK
                ThemeOption.SYSTEM.ordinal -> ThemeOption.SYSTEM
                else -> ThemeOption.DYNAMIC
            }
        }
    }

    suspend fun setTheme(themeOption: ThemeOption) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = themeOption.ordinal
        }
    }

    suspend fun saveFileAsImages(image: Pair<Uri, String>): Int {
        val file = checkAndCreateExternalParentDir(context.getString(R.string.pro_scanner_images))
        return savePdfPagesAsImages(image.first.toFile(), file)
    }
}
