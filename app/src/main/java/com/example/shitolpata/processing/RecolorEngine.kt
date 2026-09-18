package com.example.shitolpata.processing

import android.graphics.Bitmap
import android.graphics.Color
import com.example.shitolpata.model.AdvancedSettings
import com.example.shitolpata.model.Palette
import kotlin.math.abs

object RecolorEngine {

    /**
     * Recolors an input Bitmap according to the selected Palette and AdvancedSettings.
     * High performance pixel-buffer manipulation.
     */
    fun recolorBitmap(
        source: Bitmap,
        palette: Palette,
        alreadyInverted: Boolean,
        preserveImages: Boolean,
        advanced: AdvancedSettings
    ): Bitmap {
        val width = source.width
        val height = source.height
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        val bgInt = palette.bgInt
        val fgInt = palette.fgInt

        val bgR = Color.red(bgInt)
        val bgG = Color.green(bgInt)
        val bgB = Color.blue(bgInt)

        val fgR = Color.red(fgInt)
        val fgG = Color.green(fgInt)
        val fgB = Color.blue(fgInt)

        val threshold = advanced.manualThreshold
        val brightness = advanced.brightness
        val contrast = advanced.contrast
        val invThresh = 255 - threshold

        // Precompute contrast & brightness lookup table for speed (0..255)
        val lut = IntArray(256) { i ->
            var v = i
            if (brightness != 0) {
                v += brightness
            }
            if (contrast != 1.0f) {
                v = ((v - 128) * contrast + 128).toInt()
            }
            v.coerceIn(0, 255)
        }

        for (i in pixels.indices) {
            val pixel = pixels[i]
            val alpha = (pixel ushr 24) and 0xFF

            var r = (pixel shr 16) and 0xFF
            var g = (pixel shr 8) and 0xFF
            var b = pixel and 0xFF

            if (brightness != 0 || contrast != 1.0f) {
                r = lut[r]
                g = lut[g]
                b = lut[b]
            }

            // Image / photo preservation heuristic:
            // Text and line art have low color saturation (r approx g approx b).
            // Photos/drawings usually have significant saturation or chromatic divergence.
            if (preserveImages) {
                val maxC = maxOf(r, g, b)
                val minC = minOf(r, g, b)
                val chroma = maxC - minC
                if (chroma > 35) {
                    // Retain colorful photograph/illustration pixel directly
                    pixels[i] = (alpha shl 24) or (r shl 16) or (g shl 8) or b
                    continue
                }
            }

            val isBackground = if (alreadyInverted) {
                r <= invThresh && g <= invThresh && b <= invThresh
            } else {
                r >= threshold && g >= threshold && b >= threshold
            }

            if (isBackground) {
                pixels[i] = (alpha shl 24) or (bgR shl 16) or (bgG shl 8) or bgB
            } else {
                // Ink pixel: preserve relative darkness for anti-aliased typography
                val lum = (r + g + b) / 3.0f
                val darkness = if (alreadyInverted) {
                    (lum / 255.0f).coerceIn(0f, 1f)
                } else {
                    (1.0f - (lum / threshold.toFloat())).coerceIn(0f, 1f)
                }

                val outR = (bgR + (fgR - bgR) * darkness).toInt().coerceIn(0, 255)
                val outG = (bgG + (fgG - bgG) * darkness).toInt().coerceIn(0, 255)
                val outB = (bgB + (fgB - bgB) * darkness).toInt().coerceIn(0, 255)

                pixels[i] = (alpha shl 24) or (outR shl 16) or (outG shl 8) or outB
            }
        }

        output.setPixels(pixels, 0, width, 0, 0, width, height)
        return output
    }
}
