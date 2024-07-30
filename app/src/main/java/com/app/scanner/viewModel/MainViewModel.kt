package com.app.scanner.viewModel

import android.app.Activity
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.scanner.repository.Repository
import com.app.scanner.util.Preferences
import com.app.scanner.util.ThemeOption
import com.app.scanner.util.deleteGivenFiles
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {
    private val _documentList = MutableStateFlow<List<Pair<Uri, String>>>(emptyList())
    val documentList: StateFlow<List<Pair<Uri, String>>> = _documentList.asStateFlow()

    private val _categoryList = MutableStateFlow<List<String>>(emptyList())
    val categoryList: StateFlow<List<String>> = _categoryList.asStateFlow()

    private val _theme = MutableStateFlow(ThemeOption.DYNAMIC)
    val theme: StateFlow<ThemeOption> = _theme

    init {
        viewModelScope.launch {
            _theme.value = repository.getTheme().first()
        }
        getFilesIfNeeded()
    }

    fun setTheme(themeOption: ThemeOption) {
        viewModelScope.launch {
            repository.setTheme(themeOption)
            _theme.value = themeOption
        }
    }

    fun addDocument(document: Uri, category: String) {
        _documentList.value += Pair(document, category)
    }

    fun removeDocument(document: Uri, category: String) {
        _documentList.value -= Pair(document, category)
    }

    private fun getFilesIfNeeded() {
        viewModelScope.launch {
            _documentList.value = repository.fetchData()
        }
    }

    fun getCategories() {
        viewModelScope.launch {
            _categoryList.value = Preferences.getCategoryList()
        }
    }

    fun deleteSelectedFiles(context: Activity, fileList: List<Pair<Uri, String>>): Boolean {
        val notDeletedFiles = deleteGivenFiles(context, fileList)
        _documentList.value -= fileList
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

    fun setCategoryList(categoryList: List<String>) {
        Preferences.setCategoryList(categoryList)
    }

    fun addCategoryInList(category: String) {
        _categoryList.value += category
        setCategoryList(_categoryList.value)
    }

    fun removeCategoryFromList(category: String) {
        _categoryList.value -= category
        setCategoryList(_categoryList.value)
    }

    suspend fun saveFileAsImages(image: Pair<Uri, String>): Int {
        var count = 0
        viewModelScope.async {
            count = repository.saveFileAsImages(image)
        }.await()
        return count
    }
}