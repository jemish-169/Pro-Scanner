package com.app.scanner.repository

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.app.scanner.util.Constants.Companion.THEME_NAME
import com.app.scanner.util.ThemeOption
import com.app.scanner.util.checkAndCreateExternalParentDir
import com.app.scanner.util.checkAndCreateInternalParentDir
import com.app.scanner.util.getListFiles
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

@Singleton
class Repository(private val context: Activity) {

    private val _docList = mutableListOf<Pair<Uri, String>>()

    private val Context.dataStore by preferencesDataStore("theme_preferences")

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
            when (preferences[THEME_KEY] ?: ThemeOption.SYSTEM.ordinal) {
                ThemeOption.LIGHT.ordinal -> ThemeOption.LIGHT
                ThemeOption.DARK.ordinal -> ThemeOption.DARK
                ThemeOption.DYNAMIC.ordinal -> ThemeOption.DYNAMIC
                else -> ThemeOption.SYSTEM
            }
        }
    }

    suspend fun setTheme(themeOption: ThemeOption) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = themeOption.ordinal
        }
    }
}