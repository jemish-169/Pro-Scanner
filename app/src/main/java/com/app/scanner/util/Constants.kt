package com.app.scanner.util

class Constants {
    companion object {
        const val ALL = "All"
        const val DATA = "AppPrefs"
        const val THEME_NAME = "themeName"
        const val IS_ONBOARDED = "isOnboarded"
        const val PERMISSION_SHOWED = "isPermissionShowed"
        const val IS_SWIPE_TO_DELETE_ENABLE = "isSwipeToDeleteEnable"
        const val CATEGORY_LIST_ITEMS = "categoryListItems"
        val DEFAULT_CATEGORY_LIST = listOf(
            "Personal",
            "Finance",
            "Education",
            "Business",
            "Work",
            "Other"
        )
    }
}