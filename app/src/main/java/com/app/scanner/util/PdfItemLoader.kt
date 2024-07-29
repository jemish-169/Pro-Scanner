package com.app.scanner.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import java.io.File

fun pdfToBitmap(pdfFile: File): Pair<Int, String> {
    var pageCount = 0
    val createdDate = formatMillisToDate(pdfFile.lastModified())

    try {
        val renderer =
            PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY))
        pageCount = renderer.pageCount
    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    return Pair(pageCount, createdDate)
}

fun xmlToBitmap(context: Context, resourceId: Int, width: Int, height: Int): Bitmap {
    val vectorDrawable = VectorDrawableCompat.create(context.resources, resourceId, null)
        ?: return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    vectorDrawable.setBounds(50, 50, width, height)

    val bitmap = Bitmap.createBitmap(width + 50, height + 50, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)

    return bitmap
}