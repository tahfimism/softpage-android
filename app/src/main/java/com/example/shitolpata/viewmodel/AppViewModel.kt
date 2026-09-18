package com.example.shitolpata.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.shitolpata.model.*
import com.example.shitolpata.processing.DemoPdfGenerator
import com.example.shitolpata.processing.DemoPdfType
import com.example.shitolpata.processing.Otsu
import com.example.shitolpata.processing.PdfRendererHelper
import com.example.shitolpata.processing.RecolorEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val _files = MutableStateFlow<List<PdfDocumentItem>>(emptyList())
    val files: StateFlow<List<PdfDocumentItem>> = _files.asStateFlow()

    private val _activeFileIndex = MutableStateFlow(0)
    val activeFileIndex: StateFlow<Int> = _activeFileIndex.asStateFlow()

    private val _palettes = MutableStateFlow<List<Palette>>(BuiltInPalettes.list)
    val palettes: StateFlow<List<Palette>> = _palettes.asStateFlow()

    private val _selectedPalette = MutableStateFlow<Palette>(BuiltInPalettes.defaultPalette)
    val selectedPalette: StateFlow<Palette> = _selectedPalette.asStateFlow()

    private val _customBgHex = MutableStateFlow("#0F0F10")
    val customBgHex: StateFlow<String> = _customBgHex.asStateFlow()

    private val _customFgHex = MutableStateFlow("#E9E9EA")
    val customFgHex: StateFlow<String> = _customFgHex.asStateFlow()

    private val _settings = MutableStateFlow(Settings())
    val settings: StateFlow<Settings> = _settings.asStateFlow()

    private val _previewPage = MutableStateFlow(1)
    val previewPage: StateFlow<Int> = _previewPage.asStateFlow()

    private val _originalBitmap = MutableStateFlow<Bitmap?>(null)
    val originalBitmap: StateFlow<Bitmap?> = _originalBitmap.asStateFlow()

    private val _recoloredBitmap = MutableStateFlow<Bitmap?>(null)
    val recoloredBitmap: StateFlow<Bitmap?> = _recoloredBitmap.asStateFlow()

    private val _isPreviewLoading = MutableStateFlow(false)
    val isPreviewLoading: StateFlow<Boolean> = _isPreviewLoading.asStateFlow()

    private val _splitViewMode = MutableStateFlow(false)
    val splitViewMode: StateFlow<Boolean> = _splitViewMode.asStateFlow()

    private val _splitSliderPos = MutableStateFlow(0.5f)
    val splitSliderPos: StateFlow<Float> = _splitSliderPos.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _processingPage = MutableStateFlow(0)
    val processingPage: StateFlow<Int> = _processingPage.asStateFlow()

    private val _processingTotal = MutableStateFlow(0)
    val processingTotal: StateFlow<Int> = _processingTotal.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _currentTab = MutableStateFlow("design") // "design" | "adjust"
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    private val _currentScreen = MutableStateFlow("home") // "home" | "about" | "privacy"
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    private val _language = MutableStateFlow("en") // "en" | "bn"
    val language: StateFlow<String> = _language.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private var previewJob: Job? = null
    private var exportJob: Job? = null
    private var isExportCancelled = false

    val activeFile: PdfDocumentItem?
        get() = _files.value.getOrNull(_activeFileIndex.value)

    fun navigate(screen: String) {
        _currentScreen.value = screen
    }

    fun setTab(tab: String) {
        _currentTab.value = tab
    }

    fun setLanguage(lang: String) {
        _language.value = lang
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun setSplitViewMode(enabled: Boolean) {
        _splitViewMode.value = enabled
    }

    fun setSplitSliderPos(pos: Float) {
        _splitSliderPos.value = pos.coerceIn(0f, 1f)
    }

    fun setSelectedPalette(palette: Palette) {
        _selectedPalette.value = palette
        triggerRenderRecolored()
    }

    fun setCustomBg(hex: String) {
        _customBgHex.value = hex
        val updated = Palette("custom", "Custom", hex, _customFgHex.value, false)
        _selectedPalette.value = updated
        triggerRenderRecolored()
    }

    fun setCustomFg(hex: String) {
        _customFgHex.value = hex
        val updated = Palette("custom", "Custom", _customBgHex.value, hex, false)
        _selectedPalette.value = updated
        triggerRenderRecolored()
    }

    fun saveCustomPalette(name: String) {
        val newPalette = Palette(
            id = "user-${System.currentTimeMillis()}",
            name = if (name.isNotBlank()) name else "Custom Palette",
            bgHex = _customBgHex.value,
            fgHex = _customFgHex.value,
            isBuiltIn = false
        )
        _palettes.value = _palettes.value + newPalette
        _selectedPalette.value = newPalette
        showToast(if (_language.value == "en") "Palette saved!" else "প্যালেট সংরক্ষণ করা হয়েছে!")
    }

    fun deletePalette(id: String) {
        _palettes.value = _palettes.value.filter { it.id != id }
        if (_selectedPalette.value.id == id) {
            _selectedPalette.value = BuiltInPalettes.defaultPalette
            triggerRenderRecolored()
        }
    }

    fun updateSettings(updater: (Settings) -> Settings) {
        _settings.value = updater(_settings.value)
        triggerRenderRecolored()
    }

    fun setPreviewPage(page: number) {
        val total = activeFile?.pageCount ?: 1
        _previewPage.value = page.toInt().coerceIn(1, total)
        triggerRenderPage()
    }

    fun nextPage() {
        val total = activeFile?.pageCount ?: 1
        if (_previewPage.value < total) {
            _previewPage.value++
            triggerRenderPage()
        }
    }

    fun prevPage() {
        if (_previewPage.value > 1) {
            _previewPage.value--
            triggerRenderPage()
        }
    }

    fun autoDetectOtsu() {
        val original = _originalBitmap.value ?: return
        viewModelScope.launch(Dispatchers.Default) {
            val optimalThreshold = Otsu.computeOtsuThreshold(original)
            _settings.value = _settings.value.copy(
                advanced = _settings.value.advanced.copy(manualThreshold = optimalThreshold)
            )
            triggerRenderRecolored()
            showToast(
                if (_language.value == "en") "Otsu threshold applied: $optimalThreshold"
                else "ওতসু কাট-অফ প্রয়োগ করা হয়েছে: $optimalThreshold"
            )
        }
    }

    /**
     * Imports a PDF from a content Uri
     */
    fun loadPdfFromUri(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var displayName = "Document.pdf"
                var size = 0L

                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (cursor.moveToFirst()) {
                        if (nameIndex != -1) displayName = cursor.getString(nameIndex)
                        if (sizeIndex != -1) size = cursor.getLong(sizeIndex)
                    }
                }

                // Copy to cache file for clean ParcelFileDescriptor access
                val tempFile = File(context.cacheDir, "imported_${System.currentTimeMillis()}.pdf")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }

                val pfd = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
                val renderer = android.graphics.pdf.PdfRenderer(pfd)
                val pageCount = renderer.pageCount
                renderer.close()
                pfd.close()

                val docItem = PdfDocumentItem(
                    id = uri.toString(),
                    name = displayName,
                    file = tempFile,
                    uri = uri,
                    pageCount = pageCount,
                    sizeBytes = if (size > 0) size else tempFile.length()
                )

                _files.value = _files.value + docItem
                _activeFileIndex.value = _files.value.size - 1
                _previewPage.value = 1

                triggerRenderPage()
                showToast(if (_language.value == "en") "Loaded $displayName" else "$displayName লোড হয়েছে")
            } catch (e: Exception) {
                e.printStackTrace()
                showToast(if (_language.value == "en") "Error opening PDF" else "পিডিএফ খুলতে সমস্যা হয়েছে")
            }
        }
    }

    /**
     * Loads one of the offline demo PDFs
     */
    fun loadDemoPdf(type: DemoPdfType) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>()
                val file = DemoPdfGenerator.generateDemoPdf(context, type)
                val pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                val renderer = android.graphics.pdf.PdfRenderer(pfd)
                val pageCount = renderer.pageCount
                renderer.close()
                pfd.close()

                val item = PdfDocumentItem(
                    id = "demo_${type.name}",
                    name = type.displayName,
                    file = file,
                    pageCount = pageCount,
                    sizeBytes = file.length()
                )

                _files.value = _files.value + item
                _activeFileIndex.value = _files.value.size - 1
                _previewPage.value = 1

                triggerRenderPage()
                showToast(if (_language.value == "en") "Opened demo: ${type.displayName}" else "ডেমো লোড হয়েছে: ${type.displayName}")
            } catch (e: Exception) {
                e.printStackTrace()
                showToast(if (_language.value == "en") "Error creating demo PDF" else "ডেমো তৈরি করতে সমস্যা হয়েছে")
            }
        }
    }

    fun selectFile(index: Int) {
        if (index in _files.value.indices) {
            _activeFileIndex.value = index
            _previewPage.value = 1
            triggerRenderPage()
        }
    }

    fun removeFile(index: Int) {
        val currentList = _files.value.toMutableList()
        if (index in currentList.indices) {
            currentList.removeAt(index)
            _files.value = currentList
            if (_activeFileIndex.value >= currentList.size) {
                _activeFileIndex.value = (currentList.size - 1).coerceAtLeast(0)
            }
            if (currentList.isNotEmpty()) {
                _previewPage.value = 1
                triggerRenderPage()
            } else {
                _originalBitmap.value = null
                _recoloredBitmap.value = null
            }
        }
    }

    fun clearQueue() {
        _files.value = emptyList()
        _originalBitmap.value = null
        _recoloredBitmap.value = null
    }

    /**
     * Renders the current active page from scratch
     */
    private fun triggerRenderPage() {
        val fileItem = activeFile ?: return
        val targetFile = fileItem.file ?: return
        val pageNum = _previewPage.value

        previewJob?.cancel()
        previewJob = viewModelScope.launch {
            _isPreviewLoading.value = true
            try {
                val pfd = ParcelFileDescriptor.open(targetFile, ParcelFileDescriptor.MODE_READ_ONLY)
                val rawBitmap = PdfRendererHelper.renderPage(
                    pfd = pfd,
                    pageIndex = pageNum - 1,
                    dpi = _settings.value.dpi
                )
                pfd.close()

                _originalBitmap.value = rawBitmap

                // Recolored Bitmap
                val recolored = RecolorEngine.recolorBitmap(
                    source = rawBitmap,
                    palette = _selectedPalette.value,
                    alreadyInverted = _settings.value.alreadyInverted,
                    preserveImages = _settings.value.preserveImages,
                    advanced = _settings.value.advanced
                )
                _recoloredBitmap.value = recolored
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isPreviewLoading.value = false
            }
        }
    }

    /**
     * Recolors the already-rendered original bitmap without re-rendering PDF
     */
    private fun triggerRenderRecolored() {
        val original = _originalBitmap.value ?: return
        viewModelScope.launch(Dispatchers.Default) {
            val recolored = RecolorEngine.recolorBitmap(
                source = original,
                palette = _selectedPalette.value,
                alreadyInverted = _settings.value.alreadyInverted,
                preserveImages = _settings.value.preserveImages,
                advanced = _settings.value.advanced
            )
            _recoloredBitmap.value = recolored
        }
    }

    fun exportRecoloredPdf(context: Context, destUri: Uri) {
        val fileItem = activeFile ?: return
        val sourceFile = fileItem.file ?: return

        isExportCancelled = false
        _isProcessing.value = true
        _processingPage.value = 0

        val totalPages = fileItem.pageCount
        val pagesToProcess = when (_settings.value.pagesSelection) {
            PageSelectionType.ALL -> (1..totalPages).toList()
            PageSelectionType.RANGE -> (_settings.value.pagesRangeStart.._settings.value.pagesRangeEnd.coerceAtMost(totalPages)).toList()
            PageSelectionType.CUSTOM -> parseCustomPages(_settings.value.pagesCustom, totalPages)
        }
        _processingTotal.value = pagesToProcess.size

        exportJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val tempOutput = File(context.cacheDir, "recolored_export_${System.currentTimeMillis()}.pdf")
                val pfd = ParcelFileDescriptor.open(sourceFile, ParcelFileDescriptor.MODE_READ_ONLY)

                val success = PdfRendererHelper.exportRecoloredPdf(
                    pfd = pfd,
                    pagesToProcess = pagesToProcess,
                    palette = _selectedPalette.value,
                    settings = _settings.value,
                    outputFile = tempOutput,
                    isCancelled = { isExportCancelled },
                    onProgress = { current, total ->
                        _processingPage.value = current
                        _processingTotal.value = total
                    }
                )
                pfd.close()

                if (success && !isExportCancelled) {
                    context.contentResolver.openOutputStream(destUri)?.use { out ->
                        tempOutput.inputStream().use { input ->
                            input.copyTo(out)
                        }
                    }
                    withContext(Dispatchers.Main) {
                        showToast(if (_language.value == "en") "PDF exported successfully!" else "পিডিএফ সফলভাবে এক্সপোর্ট হয়েছে!")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showToast(if (_language.value == "en") "Export failed" else "এক্সপোর্ট ব্যর্থ হয়েছে")
                }
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun exportCurrentPageImage(context: Context, destUri: Uri) {
        val recolored = _recoloredBitmap.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                context.contentResolver.openOutputStream(destUri)?.use { out ->
                    recolored.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                withContext(Dispatchers.Main) {
                    showToast(if (_language.value == "en") "Page image saved!" else "পৃষ্ঠার ছবি সংরক্ষিত হয়েছে!")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showToast(if (_language.value == "en") "Failed to save image" else "ছবি সংরক্ষণে সমস্যা হয়েছে")
                }
            }
        }
    }

    fun cancelProcessing() {
        isExportCancelled = true
        exportJob?.cancel()
        _isProcessing.value = false
        showToast(if (_language.value == "en") "Export cancelled" else "এক্সপোর্ট বাতিল করা হয়েছে")
    }

    private fun parseCustomPages(customStr: String, total: Int): List<Int> {
        val result = mutableSetOf<Int>()
        val parts = customStr.split(",")
        for (part in parts) {
            val trimmed = part.trim()
            if (trimmed.contains("-")) {
                val rangeParts = trimmed.split("-")
                val start = rangeParts.getOrNull(0)?.trim()?.toIntOrNull() ?: continue
                val end = rangeParts.getOrNull(1)?.trim()?.toIntOrNull() ?: continue
                for (p in start..end) {
                    if (p in 1..total) result.add(p)
                }
            } else {
                val p = trimmed.toIntOrNull() ?: continue
                if (p in 1..total) result.add(p)
            }
        }
        return if (result.isEmpty()) (1..total).toList() else result.sorted()
    }
}
