package com.example.shitolpata.model

enum class PageSelectionType {
    ALL,
    RANGE,
    CUSTOM
}

data class AdvancedSettings(
    val manualThreshold: Int = 220, // 100 to 250
    val brightness: Int = 0,         // -50 to +50
    val contrast: Float = 1.0f       // 0.5f to 1.8f
)

data class Settings(
    val dpi: Int = 150, // 72 to 300
    val alreadyInverted: Boolean = false,
    val preserveImages: Boolean = false,
    val pagesSelection: PageSelectionType = PageSelectionType.ALL,
    val pagesRangeStart: Int = 1,
    val pagesRangeEnd: Int = 1,
    val pagesCustom: String = "",
    val advanced: AdvancedSettings = AdvancedSettings()
)
