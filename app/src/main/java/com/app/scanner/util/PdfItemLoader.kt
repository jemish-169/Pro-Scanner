package com.app.scanner.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.app.scanner.R
import java.io.File

fun pdfToBitmap(pdfFile: File, context: Activity): Triple<ImageBitmap, Int, String> {
    var bitmap: Bitmap? = null
    var pageCount = 0
    val createdDate = formatMillisToDate(pdfFile.lastModified())

    try {
        val renderer =
            PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY))
        pageCount = renderer.pageCount
        if (pageCount > 0) {
            val page = renderer.openPage(0)
            bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)

            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            renderer.close()
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    val returnBitmap = bitmap?.asImageBitmap() ?: xmlToBitmap(
        context = context,
        R.drawable.ic_file,
        300,
        300
    ).asImageBitmap()

    return Triple(returnBitmap, pageCount, createdDate)
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