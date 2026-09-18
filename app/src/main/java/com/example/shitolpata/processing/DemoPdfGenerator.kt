package com.example.shitolpata.processing

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

enum class DemoPdfType(val displayName: String, val filename: String) {
    MATH_WORKSHEET("Math Worksheet", "math_worksheet_demo.pdf"),
    BOOK_CHAPTER("Book Chapter", "book_chapter_demo.pdf"),
    LEGAL_NDA("Legal Agreement", "legal_nda_demo.pdf")
}

object DemoPdfGenerator {

    suspend fun generateDemoPdf(context: Context, type: DemoPdfType): File = withContext(Dispatchers.IO) {
        val destFile = File(context.cacheDir, type.filename)
        val document = PdfDocument()

        val pageWidth = 595 // A4 standard width in points
        val pageHeight = 842 // A4 standard height in points

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // Background
        canvas.drawColor(Color.WHITE)

        val textPaint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
            textSize = 12f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        }

        val headerPaint = Paint().apply {
            color = Color.rgb(24, 32, 28)
            isAntiAlias = true
            textSize = 18f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }

        val subPaint = Paint().apply {
            color = Color.DKGRAY
            isAntiAlias = true
            textSize = 10f
        }

        val linePaint = Paint().apply {
            color = Color.GRAY
            strokeWidth = 1.5f
        }

        when (type) {
            DemoPdfType.MATH_WORKSHEET -> {
                canvas.drawText("Shitol Pata Academic Sandbox", 50f, 60f, subPaint)
                canvas.drawText("Mathematics Worksheet: Geometry & Calculus", 50f, 95f, headerPaint)
                canvas.drawText("Date: September 2026 | Student: ___________________", 50f, 120f, subPaint)
                canvas.drawLine(50f, 135f, (pageWidth - 50).toFloat(), 135f, linePaint)

                // Question 1
                headerPaint.textSize = 14f
                canvas.drawText("Section A: Calculus Foundations", 50f, 170f, headerPaint)
                canvas.drawText("1. Evaluate the limit of the function as x approaches 2:", 50f, 200f, textPaint)
                textPaint.typeface = Typeface.MONOSPACE
                canvas.drawText("   lim (x^2 - 4) / (x - 2)", 70f, 225f, textPaint)

                textPaint.typeface = Typeface.SANS_SERIF
                canvas.drawText("2. Find the first derivative of the following equation:", 50f, 265f, textPaint)
                textPaint.typeface = Typeface.MONOSPACE
                canvas.drawText("   f(x) = 3x^3 - 5x^2 + 8x - 12", 70f, 290f, textPaint)

                // Section B: Geometry
                textPaint.typeface = Typeface.SANS_SERIF
                canvas.drawText("Section B: Geometry & Trigonometry", 50f, 340f, headerPaint)
                canvas.drawText("3. Given the right-angled triangle below, solve for hypotenuse c:", 50f, 370f, textPaint)

                // Draw Triangle
                val shapePaint = Paint().apply {
                    color = Color.BLACK
                    style = Paint.Style.STROKE
                    strokeWidth = 2f
                    isAntiAlias = true
                }
                canvas.drawLine(100f, 500f, 250f, 500f, shapePaint)
                canvas.drawLine(100f, 500f, 100f, 410f, shapePaint)
                canvas.drawLine(100f, 410f, 250f, 500f, shapePaint)

                canvas.drawText("a = 8 cm", 45f, 460f, subPaint)
                canvas.drawText("b = 15 cm", 150f, 520f, subPaint)
                canvas.drawText("c = ?", 190f, 445f, textPaint)

                // Unit Circle diagram
                canvas.drawText("4. Coordinate representation inside the unit square:", 50f, 560f, textPaint)
                canvas.drawRect(350f, 590f, 470f, 710f, shapePaint)
                val circlePaint = Paint(shapePaint).apply { color = Color.rgb(0, 120, 200) }
                canvas.drawCircle(410f, 650f, 60f, circlePaint)
                canvas.drawText("r = 5 cm", 415f, 645f, subPaint)
            }

            DemoPdfType.BOOK_CHAPTER -> {
                canvas.drawText("THE COGNITIVE AGE", 50f, 60f, subPaint)
                canvas.drawText("CHAPTER 7", (pageWidth - 120).toFloat(), 60f, subPaint)
                canvas.drawLine(50f, 75f, (pageWidth - 50).toFloat(), 75f, linePaint)

                canvas.drawText("7.4 Artificial Intelligence & Symbolism", 50f, 115f, headerPaint)

                val bodyPaint = Paint().apply {
                    color = Color.BLACK
                    textSize = 11f
                    isAntiAlias = true
                }

                val p1 = "The quest to simulate human intelligence through algorithmic computation began " +
                        "with the premise that all logical thinking could be represented as symbolic manipulation. " +
                        "Early pioneers hypothesized that by translating concepts into formal mathematics, " +
                        "machines could reason, solve complex theorems, and understand natural languages."
                drawWrappedText(canvas, p1, 50f, 150f, (pageWidth - 100).toFloat(), bodyPaint)

                val p2 = "This realization catalyzed the shift toward connectionism and neural networks. " +
                        "Instead of hardcoding rules, researchers aimed to mimic the biological architectures of the brain. " +
                        "By layering simple artificial neurons and optimizing weights across large datasets, " +
                        "connectionist models acquired representations dynamically."
                drawWrappedText(canvas, p2, 50f, 230f, (pageWidth - 100).toFloat(), bodyPaint)

                // Diagram Box with color
                val boxPaint = Paint().apply {
                    color = Color.rgb(235, 240, 245)
                    style = Paint.Style.FILL
                }
                canvas.drawRect(80f, 320f, (pageWidth - 80).toFloat(), 500f, boxPaint)
                val borderPaint = Paint().apply {
                    color = Color.LTGRAY
                    style = Paint.Style.STROKE
                    strokeWidth = 1.5f
                }
                canvas.drawRect(80f, 320f, (pageWidth - 80).toFloat(), 500f, borderPaint)

                // Colorful nodes
                val redPaint = Paint().apply { color = Color.rgb(200, 50, 50); style = Paint.Style.FILL; isAntiAlias = true }
                val greenPaint = Paint().apply { color = Color.rgb(40, 160, 50); style = Paint.Style.FILL; isAntiAlias = true }
                val bluePaint = Paint().apply { color = Color.rgb(40, 80, 210); style = Paint.Style.FILL; isAntiAlias = true }

                canvas.drawCircle(140f, 370f, 12f, redPaint)
                canvas.drawCircle(140f, 440f, 12f, redPaint)
                canvas.drawCircle(280f, 380f, 12f, greenPaint)
                canvas.drawCircle(280f, 430f, 12f, greenPaint)
                canvas.drawCircle(420f, 410f, 12f, bluePaint)

                canvas.drawText("Figure 7.2: Architectural Topology of a Multi-Layer Perceptron.", 85f, 525f, subPaint)

                val p3 = "As shown in Figure 7.2, signal propagation flows feedforward from input nodes " +
                        "through parameterized hidden transformations. Gradient descent backpropagation " +
                        "adjusts weights iteratively to minimize loss functions."
                drawWrappedText(canvas, p3, 50f, 560f, (pageWidth - 100).toFloat(), bodyPaint)
            }

            DemoPdfType.LEGAL_NDA -> {
                val titlePaint = Paint().apply {
                    color = Color.BLACK
                    textSize = 15f
                    typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                    isAntiAlias = true
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText("MUTUAL NON-DISCLOSURE AGREEMENT", pageWidth / 2f, 80f, titlePaint)

                val bodyPaint = Paint().apply {
                    color = Color.BLACK
                    textSize = 10f
                    isAntiAlias = true
                }

                val intro = "This Mutual Non-Disclosure Agreement (the \"Agreement\") is entered into as of " +
                        "September 2026, by and between SoftPage Technologies and the Recipient User."
                drawWrappedText(canvas, intro, 50f, 120f, (pageWidth - 100).toFloat(), bodyPaint)

                headerPaint.textSize = 11f
                canvas.drawText("1. Definition of Confidential Information", 50f, 170f, headerPaint)
                val sec1 = "Confidential Information refers to proprietary algorithms, tonal mapping palettes, " +
                        "PDF processing pipelines, source code, and design representations disclosed during evaluation."
                drawWrappedText(canvas, sec1, 50f, 190f, (pageWidth - 100).toFloat(), bodyPaint)

                canvas.drawText("2. Privacy & On-Device Security", 50f, 250f, headerPaint)
                val sec2 = "All document processing and color alterations are conducted strictly within the local device " +
                        "runtime. Zero documents or metadata are transmitted to external cloud systems."
                drawWrappedText(canvas, sec2, 50f, 270f, (pageWidth - 100).toFloat(), bodyPaint)

                // Official Seal
                val sealPaint = Paint().apply {
                    color = Color.rgb(40, 120, 60)
                    style = Paint.Style.STROKE
                    strokeWidth = 2f
                    isAntiAlias = true
                }
                canvas.drawRect(230f, 550f, 365f, 615f, sealPaint)
                val sealText = Paint().apply {
                    color = Color.rgb(40, 120, 60)
                    textSize = 13f
                    typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText("VERIFIED LOCAL", 297f, 580f, sealText)
                sealText.textSize = 9f
                canvas.drawText("100% PRIVATE", 297f, 600f, sealText)
            }
        }

        // Footer
        canvas.drawText("Page 1 of 1 — Shitol Pata Engine", (pageWidth / 2f) - 60f, (pageHeight - 30).toFloat(), subPaint)

        document.finishPage(page)
        FileOutputStream(destFile).use { out ->
            document.writeTo(out)
        }
        document.close()

        destFile
    }

    private fun drawWrappedText(canvas: Canvas, text: String, x: Float, y: Float, maxWidth: Float, paint: Paint) {
        val words = text.split(" ")
        var currentY = y
        var currentLine = ""

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val width = paint.measureText(testLine)
            if (width > maxWidth) {
                canvas.drawText(currentLine, x, currentY, paint)
                currentLine = word
                currentY += paint.textSize + 4f
            } else {
                currentLine = testLine
            }
        }
        if (currentLine.isNotEmpty()) {
            canvas.drawText(currentLine, x, currentY, paint)
        }
    }
}
