package com.example.shitolpata.model

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

data class Palette(
    val id: String,
    val name: String,
    val bgHex: String,
    val fgHex: String,
    val isBuiltIn: Boolean = true
) {
    val bgColor: Color
        get() = try {
            Color(bgHex.toColorInt())
        } catch (e: Exception) {
            Color(0xFF0F0F10)
        }

    val fgColor: Color
        get() = try {
            Color(fgHex.toColorInt())
        } catch (e: Exception) {
            Color(0xFFE9E9EA)
        }

    val bgInt: Int
        get() = try {
            bgHex.toColorInt()
        } catch (e: Exception) {
            0xFF0F0F10.toInt()
        }

    val fgInt: Int
        get() = try {
            fgHex.toColorInt()
        } catch (e: Exception) {
            0xFFE9E9EA.toInt()
        }
}

object BuiltInPalettes {
    val list: List<Palette> = listOf(
        Palette(
            id = "classic-dark",
            name = "Classic Dark",
            bgHex = "#0F0F10",
            fgHex = "#E9E9EA"
        ),
        Palette(
            id = "warm-sepia",
            name = "Warm Sepia",
            bgHex = "#F4ECD8",
            fgHex = "#2A1D12"
        ),
        Palette(
            id = "soft-gray",
            name = "Soft Gray",
            bgHex = "#F2F3F5",
            fgHex = "#202225"
        ),
        Palette(
            id = "night-blue",
            name = "Night Blue",
            bgHex = "#0B1220",
            fgHex = "#D7E3FF"
        ),
        Palette(
            id = "cream-ink",
            name = "Cream + Ink",
            bgHex = "#FFF7E6",
            fgHex = "#10131A"
        ),
        Palette(
            id = "pure-bw",
            name = "Pure B/W",
            bgHex = "#FFFFFF",
            fgHex = "#000000"
        ),
        Palette(
            id = "amoled",
            name = "AMOLED",
            bgHex = "#000000",
            fgHex = "#FFFFFF"
        ),
        Palette(
            id = "low-glare-green",
            name = "Low-glare Green",
            bgHex = "#0B1A12",
            fgHex = "#D6FFE6"
        )
    )

    val defaultPalette: Palette = list[0]
}
