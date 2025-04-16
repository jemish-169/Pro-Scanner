package com.elite.scanner.repository

import android.app.Activity
import android.net.Uri
import com.elite.scanner.util.checkAndCreateInternalParentDir
import com.elite.scanner.util.getListFiles
import kotlinx.coroutines.coroutineScope
import javax.inject.Singleton

@Singleton
class Repository(private val context: Activity) {

    private val _docList = mutableListOf<Pair<Uri, String>>()

    suspend fun fetchData(): List<Pair<Uri, String>> {
        coroutineScope {
            checkAndCreateInternalParentDir(context).let { directory ->
                _docList.addAll(getListFiles(directory))
            }
        }
        return _docList
    }
}
