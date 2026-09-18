package com.example.shitolpata.processing

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import com.example.shitolpata.model.Palette
import com.example.shitolpata.model.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object PdfRendererHelper {

    /**
     * Renders a single page from a ParcelFileDescriptor to a clean Bitmap.
     */
    suspend fun renderPage(
        pfd: ParcelFileDescriptor,
        pageIndex: Int,
        dpi: Int = 150
    ): Bitmap = withContext(Dispatchers.IO) {
        val renderer = PdfRenderer(pfd)
        try {
            val pageCount = renderer.pageCount
            val safeIndex = pageIndex.coerceIn(0, pageCount - 1)
            val page = renderer.openPage(safeIndex)
            try {
                val scale = dpi / 72.0f
                val destWidth = (page.width * scale).toInt().coerceAtLeast(100)
                val destHeight = (page.height * scale).toInt().coerceAtLeast(100)

                val bitmap = Bitmap.createBitmap(destWidth, destHeight, Bitmap.Config.ARGB_8888)
                // Fill with white background first for transparency support
                val canvas = Canvas(bitmap)
                canvas.drawColor(Color.WHITE)

                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bitmap
            } finally {
                page.close()
            }
        } finally {
            renderer.close()
        }
    }

    /**
     * Exports a recolored PDF file to the destination file with progress callbacks.
     */
    suspend fun exportRecoloredPdf(
        pfd: ParcelFileDescriptor,
        pagesToProcess: List<Int>,
        palette: Palette,
        settings: Settings,
        outputFile: File,
        isCancelled: () -> Boolean,
        onProgress: (Int, Int) -> Unit
    ): Boolean = withContext(Dispatchers.IO) {
        val renderer = PdfRenderer(pfd)
        val pdfDoc = PdfDocument()

        try {
            val total = pagesToProcess.size
            val paint = Paint(Paint.FILTER_BITMAP_FLAG)

            for ((index, pageNum) in pagesToProcess.withIndex()) {
                if (isCancelled()) {
                    return@withContext false
                }

                val pageIndex = (pageNum - 1).coerceIn(0, renderer.pageCount - 1)
                val pdfPage = renderer.openPage(pageIndex)

                val pageWidth = pdfPage.width
                val pageHeight = pdfPage.height

                val scale = settings.dpi / 72.0f
                val renderWidth = (pageWidth * scale).toInt().coerceAtLeast(100)
                val renderHeight = (pageHeight * scale).toInt().coerceAtLeast(100)

                val rawBitmap = Bitmap.createBitmap(renderWidth, renderHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(rawBitmap)
                canvas.drawColor(Color.WHITE)
                pdfPage.render(rawBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
                pdfPage.close()

                val recoloredBitmap = RecolorEngine.recolorBitmap(
                    source = rawBitmap,
                    palette = palette,
                    alreadyInverted = settings.alreadyInverted,
                    preserveImages = settings.preserveImages,
                    advanced = settings.advanced
                )
                rawBitmap.recycle()

                // Create PDF page with matching dimensions (in standard PDF points 72dpi)
                val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, index + 1).create()
                val docPage = pdfDoc.startPage(pageInfo)

                val docCanvas = docPage.canvas
                val destRect = Rect(0, 0, pageWidth, pageHeight)
                docCanvas.drawBitmap(recoloredBitmap, null, destRect, paint)
                pdfDoc.finishPage(docPage)

                recoloredBitmap.recycle()

                onProgress(index + 1, total)
            }

            FileOutputStream(outputFile).use { out ->
                pdfDoc.writeTo(out)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            pdfDoc.close()
            renderer.close()
        }
    }
}
