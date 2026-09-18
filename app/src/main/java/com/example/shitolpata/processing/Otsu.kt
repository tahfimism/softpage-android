package com.example.shitolpata.processing

import android.graphics.Bitmap

object Otsu {

    /**
     * Computes the optimal threshold for a grayscale image using Otsu's Thresholding Method.
     * Chooses a threshold that maximizes the between-class variance of the foreground and background.
     */
    fun computeOtsuThreshold(bitmap: Bitmap): Int {
        val width = bitmap.width
        val height = bitmap.height
        val totalSampledPixels = width * height

        // For large bitmaps, sample pixels with a stride to stay fast
        val stride = if (totalSampledPixels > 500_000) 4 else 1

        val histogram = IntArray(256)
        var sampledCount = 0

        val rowPixels = IntArray(width)
        for (y in 0 until height step stride) {
            bitmap.getPixels(rowPixels, 0, width, 0, y, width, 1)
            for (x in 0 until width step stride) {
                val pixel = rowPixels[x]
                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF

                val gray = (0.299 * r + 0.587 * g + 0.114 * b).toInt().coerceIn(0, 255)
                histogram[gray]++
                sampledCount++
            }
        }

        if (sampledCount == 0) return 220

        var sum = 0.0
        for (i in 0 until 256) {
            sum += i * histogram[i]
        }

        var sumB = 0.0
        var wB = 0.0
        var varMax = 0.0
        var threshold = 220

        for (t in 0 until 256) {
            wB += histogram[t]
            if (wB == 0.0) continue

            val wF = sampledCount - wB
            if (wF == 0.0) break

            sumB += t * histogram[t]

            val mB = sumB / wB
            val mF = (sum - sumB) / wF

            val varBetween = wB * wF * (mB - mF) * (mB - mF)

            if (varBetween > varMax) {
                varMax = varBetween
                threshold = t
            }
        }

        return threshold.coerceIn(100, 240)
    }
}
