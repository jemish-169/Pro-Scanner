package com.app.scanner.util

import androidx.annotation.DrawableRes
import com.app.scanner.R

sealed class OnBoardingPage(
    @DrawableRes
    val image: Int,
    val title: String,
    val description: String
) {
    data object First : OnBoardingPage(
        image = R.drawable.scan_docs,
        title = "Scan Anytime, Anywhere",
        description = "Scan documents instantly with your phone's camera."
    )

    data object Second : OnBoardingPage(
        image = R.drawable.organize_docs,
        title = "Organize files",
        description = "Organize and access your files anytime, anywhere."
    )

    data object Third : OnBoardingPage(
        image = R.drawable.share_ldoc,
        title = "Share Documents",
        description = "Share scanned documents securely in just a few taps."
    )
}