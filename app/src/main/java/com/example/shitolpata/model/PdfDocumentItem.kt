package com.example.shitolpata.model

import android.net.Uri
import java.io.File

data class PdfDocumentItem(
    val id: String,
    val name: String,
    val file: File? = null,
    val uri: Uri? = null,
    val pageCount: Int = 0,
    val sizeBytes: Long = 0L
) {
    val formattedSize: String
        get() = when {
            sizeBytes < 1024 -> "$sizeBytes B"
            sizeBytes < 1024 * 1024 -> String.format("%.1f KB", sizeBytes / 1024.0)
            else -> String.format("%.1f MB", sizeBytes / (1024.0 * 1024.0))
        }
}
