package com.app.scanner.repository

import android.app.Activity
import android.net.Uri
import com.app.scanner.util.checkAndCreateDirectory
import com.app.scanner.util.checkAndCreateInternalDirectory
import com.app.scanner.util.getListFiles

class Repository(private val context: Activity) {

    private val _docList = mutableListOf<Uri>()

    fun fetchData(): List<Uri> {
        checkAndCreateDirectory().let { directory ->
            _docList.addAll(getListFiles(directory.absolutePath))
        }
        checkAndCreateInternalDirectory(context).let { directory ->
            _docList.addAll(getListFiles(directory.absolutePath))
        }
        return _docList
    }
}