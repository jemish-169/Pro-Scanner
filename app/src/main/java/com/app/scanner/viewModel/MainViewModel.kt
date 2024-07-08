package com.app.scanner.viewModel

import android.app.Activity
import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.scanner.repository.Repository
import com.app.scanner.util.Preferences
import com.app.scanner.util.deleteGivenFiles
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {
    private val _documentList = MutableStateFlow<List<Uri>>(emptyList())
    val documentList: StateFlow<List<Uri>> = _documentList.asStateFlow()

    private var isDataFetched = false

    fun addDocument(document: Uri) {
        _documentList.value += document
    }

    fun fetchDataIfNeeded() {
        viewModelScope.launch {
            if (!isDataFetched) {
                _documentList.value = repository.fetchData()
                isDataFetched = true
            }
        }
    }

    fun deleteSelectedFiles(context: Activity, fileList: List<Uri>): Boolean {
        val notDeletedFiles = deleteGivenFiles(context, fileList)
        _documentList.update { currentList ->
            currentList.filter { uri ->
                uri.toFile().exists()
            }
        }
        return notDeletedFiles.isEmpty()
    }

    fun setOnboarded(isOnboarded: Boolean) {
        Preferences.setOnboarded(isOnboarded = isOnboarded)
    }

    fun getOnboarded(): Boolean {
        return Preferences.getOnboarded()
    }

    fun setIsSwipeToDeleteEnable(isSwipeToDeleteEnable: Boolean) {
        Preferences.setIsSwipeToDeleteEnable(isSwipeToDeleteEnable = isSwipeToDeleteEnable)
    }

    fun getIsSwipeToDeleteEnable(): Boolean {
        return Preferences.getIsSwipeToDeleteEnable()
    }
}