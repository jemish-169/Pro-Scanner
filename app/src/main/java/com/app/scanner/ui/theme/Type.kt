package com.app.scanner.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.app.scanner.R

val rubikFamily = FontFamily(
    fonts = listOf(
        Font(R.font.rubik_light, FontWeight.Light),
        Font(R.font.rubik_light_italic, FontWeight.Light),
        Font(R.font.rubik_black, FontWeight.Normal),
        Font(R.font.rubik_black_italic, FontWeight.Normal),
        Font(R.font.rubik_italic, FontWeight.Normal),
        Font(R.font.rubik_regular, FontWeight.Normal),
        Font(R.font.rubik_medium, FontWeight.Medium),
        Font(R.font.rubik_medium_italic, FontWeight.Medium),
        Font(R.font.rubik_semi_bold, FontWeight.SemiBold),
        Font(R.font.rubik_semi_bold_italic, FontWeight.SemiBold),
        Font(R.font.rubik_bold, FontWeight.Bold),
        Font(R.font.rubik_bold_italic, FontWeight.Bold),
        Font(R.font.rubik_extra_bold_italic, FontWeight.Bold),
        Font(R.font.rubik_extra_bold, FontWeight.ExtraBold)
    )
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = rubikFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = rubikFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = rubikFamily,
        fontWeight = FontWeight.Light,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = rubikFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = rubikFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = rubikFamily,
        fontWeight = FontWeight.Light,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = rubikFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = rubikFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = rubikFamily,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = rubikFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = rubikFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = rubikFamily,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = rubikFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = rubikFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = rubikFamily,
        fontWeight = FontWeight.Light,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)