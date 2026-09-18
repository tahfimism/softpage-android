package com.example.shitolpata.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shitolpata.model.Palette
import com.example.shitolpata.model.PdfDocumentItem
import com.example.shitolpata.model.Settings
import com.example.shitolpata.processing.DemoPdfType
import com.example.shitolpata.ui.components.*
import com.example.shitolpata.ui.theme.LeafPrimary
import com.example.shitolpata.ui.theme.PaperLeaf
import com.example.shitolpata.ui.theme.SunlightSecondary
import com.example.shitolpata.viewmodel.AppViewModel

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val files by viewModel.files.collectAsState()
    val activeIndex by viewModel.activeFileIndex.collectAsState()
    val palettes by viewModel.palettes.collectAsState()
    val selectedPalette by viewModel.selectedPalette.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val previewPage by viewModel.previewPage.collectAsState()
    val originalBitmap by viewModel.originalBitmap.collectAsState()
    val recoloredBitmap by viewModel.recoloredBitmap.collectAsState()
    val isPreviewLoading by viewModel.isPreviewLoading.collectAsState()
    val splitMode by viewModel.splitViewMode.collectAsState()
    val splitSliderPos by viewModel.splitSliderPos.collectAsState()
    val language by viewModel.language.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val isEnglish = language == "en"

    var showCustomColorDialog by remember { mutableStateOf(false) }

    // SAF File Open Launcher
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { viewModel.loadPdfFromUri(context, it) }
    }

    // SAF PDF Save Launcher
    val pdfExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri: Uri? ->
        uri?.let { viewModel.exportRecoloredPdf(context, it) }
    }

    // SAF Image Save Launcher
    val imageExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("image/png")
    ) { uri: Uri? ->
        uri?.let { viewModel.exportCurrentPageImage(context, it) }
    }

    val activeDoc = files.getOrNull(activeIndex)

    if (files.isEmpty()) {
        // Clean Homepage: Centered Logo & Name in middle, NO feature lists, NO palettes/adjust icons, NO bottom bar
        CleanHomepageView(
            isEnglish = isEnglish,
            onPickPdf = { pdfPickerLauncher.launch(arrayOf("application/pdf")) },
            onSelectDemo = { type -> viewModel.loadDemoPdf(type) },
            modifier = modifier
        )
    } else {
        // Document Workspace (When document is opened)
        Column(
            modifier = modifier
                .fillMaxSize()
                .testTag("workspace_screen")
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 12.dp)
            ) {
                // Document queue pill (if multiple docs or to add more)
                item {
                    DocumentQueuePhoneBar(
                        files = files,
                        activeIndex = activeIndex,
                        isEnglish = isEnglish,
                        onSelectFile = { viewModel.selectFile(it) },
                        onRemoveFile = { viewModel.removeFile(it) },
                        onAddMore = { pdfPickerLauncher.launch(arrayOf("application/pdf")) }
                    )
                }

                // Document Preview Card
                item {
                    PagePreviewComponent(
                        originalBitmap = originalBitmap,
                        recoloredBitmap = recoloredBitmap,
                        currentPage = previewPage,
                        totalPages = activeDoc?.pageCount ?: 1,
                        isLoading = isPreviewLoading,
                        splitMode = splitMode,
                        splitSliderPos = splitSliderPos,
                        isEnglish = isEnglish,
                        onPreviousPage = { viewModel.prevPage() },
                        onNextPage = { viewModel.nextPage() },
                        onToggleSplitMode = { viewModel.setSplitViewMode(!splitMode) },
                        onSplitSliderChange = { viewModel.setSplitSliderPos(it) },
                        onAutoOtsu = { viewModel.autoDetectOtsu() }
                    )
                }

                // Workspace Mode Switcher: Color Palettes vs Fine Adjustments
                item {
                    TabRow(
                        selectedTabIndex = if (currentTab == "design") 0 else 1,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = LeafPrimary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .testTag("workspace_tabs")
                    ) {
                        Tab(
                            selected = currentTab == "design",
                            onClick = { viewModel.setTab("design") },
                            text = {
                                Text(
                                    text = if (isEnglish) "Palettes" else "রঙের স্কিম",
                                    fontWeight = if (currentTab == "design") FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            icon = { Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                        Tab(
                            selected = currentTab == "adjust",
                            onClick = { viewModel.setTab("adjust") },
                            text = {
                                Text(
                                    text = if (isEnglish) "Fine Tuning" else "অ্যাডজাস্টমেন্ট",
                                    fontWeight = if (currentTab == "adjust") FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            icon = { Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                    }
                }

                // Active Workspace Tab Content
                if (currentTab == "design") {
                    item {
                        PaletteSelector(
                            palettes = palettes,
                            selectedPalette = selectedPalette,
                            isEnglish = isEnglish,
                            onSelectPalette = { viewModel.setSelectedPalette(it) },
                            onOpenCustomPicker = { showCustomColorDialog = true },
                            onDeletePalette = { viewModel.deletePalette(it) }
                        )
                    }
                } else {
                    item {
                        AdvancedControlsView(
                            settings = settings,
                            isEnglish = isEnglish,
                            onUpdateSettings = { viewModel.updateSettings(it) },
                            onAutoOtsu = { viewModel.autoDetectOtsu() }
                        )
                    }
                    item {
                        PageRangeSelectorView(
                            settings = settings,
                            totalPages = activeDoc?.pageCount ?: 1,
                            isEnglish = isEnglish,
                            onUpdateSettings = { viewModel.updateSettings(it) }
                        )
                    }
                }
            }

            // Fixed Bottom Action Bar for Exporting
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            val defaultImgName = "${activeDoc?.name?.substringBeforeLast(".") ?: "page"}_recolored_p$previewPage.png"
                            imageExportLauncher.launch(defaultImgName)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("btn_save_page_image")
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isEnglish) "Save PNG" else "ছবি সেভ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = {
                            val defaultPdfName = "${activeDoc?.name?.substringBeforeLast(".") ?: "document"}_softpage.pdf"
                            pdfExportLauncher.launch(defaultPdfName)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LeafPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(46.dp)
                            .testTag("btn_export_recolored_pdf")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isEnglish) "Export PDF" else "পিডিএফ এক্সপোর্ট",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (showCustomColorDialog) {
        CustomColorPickerDialog(
            initialBgHex = selectedPalette.bgHex,
            initialFgHex = selectedPalette.fgHex,
            isEnglish = isEnglish,
            onDismiss = { showCustomColorDialog = false },
            onSavePalette = { name, bg, fg ->
                viewModel.setCustomBg(bg)
                viewModel.setCustomFg(fg)
                viewModel.saveCustomPalette(name)
                showCustomColorDialog = false
            }
        )
    }
}

/**
 * Clean Homepage:
 * - Centered Logo & Name in middle
 * - Primary Action Button to Choose PDF
 * - Instant Demo Files
 * - NO extra feature lists
 * - NO palettes or adjust icon on home page
 * - NO bottom bar
 */
@Composable
fun CleanHomepageView(
    isEnglish: Boolean,
    onPickPdf: () -> Unit,
    onSelectDemo: (DemoPdfType) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .navigationBarsPadding()
            .testTag("landing_view"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Authentic Shitol Pata Brand Emblem
            ShitolPataLogo(
                modifier = Modifier
                    .size(80.dp)
                    .testTag("brand_logo")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Centered Title
            Text(
                text = if (isEnglish) "Shitol Pata" else "শীতল পাতা",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Tagline
            Text(
                text = if (isEnglish)
                    "A soothing sheet for your digital documents"
                else
                    "ডিজিটাল ডকুমেন্টের জন্য এক টুকরো শীতল পাতা",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = LeafPrimary,
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle
            Text(
                text = if (isEnglish)
                    "Turn harsh, blinding PDFs into soft, eye-friendly documents."
                else
                    "তীব্র উজ্জ্বল পিডিএফগুলোকে রূপান্তর করুন কোমল ও আরামদায়ক রঙে।",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Primary Choose PDF Document Button
            Button(
                onClick = onPickPdf,
                colors = ButtonDefaults.buttonColors(containerColor = LeafPrimary),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("btn_choose_pdf")
            ) {
                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isEnglish) "Choose PDF Document" else "পিডিএফ ফাইল সিলেক্ট করুন",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sample Document Buttons
            Text(
                text = if (isEnglish) "Or try a sample document" else "অথবা ডেমো ফাইল দিয়ে পরখ করুন",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DemoPillChip(
                    title = if (isEnglish) "Math Sheet" else "গণিত",
                    onClick = { onSelectDemo(DemoPdfType.MATH_WORKSHEET) },
                    modifier = Modifier.weight(1f).testTag("btn_demo_math")
                )

                DemoPillChip(
                    title = if (isEnglish) "Book Chapter" else "বইয়ের অধ্যায়",
                    onClick = { onSelectDemo(DemoPdfType.BOOK_CHAPTER) },
                    modifier = Modifier.weight(1f).testTag("btn_demo_book")
                )

                DemoPillChip(
                    title = if (isEnglish) "Legal NDA" else "চুক্তিপত্র",
                    onClick = { onSelectDemo(DemoPdfType.LEGAL_NDA) },
                    modifier = Modifier.weight(1f).testTag("btn_demo_nda")
                )
            }
        }
    }
}

@Composable
fun DemoPillChip(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = modifier.height(44.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun DocumentQueuePhoneBar(
    files: List<PdfDocumentItem>,
    activeIndex: Int,
    isEnglish: Boolean,
    onSelectFile: (Int) -> Unit,
    onRemoveFile: (Int) -> Unit,
    onAddMore: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEnglish) "Document (${files.size})" else "ডকুমেন্ট (${files.size})",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )

                FilledTonalButton(
                    onClick = onAddMore,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isEnglish) "Add" else "যোগ", fontSize = 11.sp)
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(files) { index, doc ->
                    val isCurrent = index == activeIndex
                    Surface(
                        onClick = { onSelectFile(index) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isCurrent) LeafPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isCurrent) BorderStroke(1.5.dp, LeafPrimary) else null,
                        modifier = Modifier.height(42.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = if (isCurrent) LeafPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Column {
                                Text(
                                    text = doc.name,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${doc.pageCount} p • ${doc.formattedSize}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = { onRemoveFile(index) },
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShitolPataLogo(
    modifier: Modifier = Modifier,
    backgroundColor: Color = LeafPrimary,
    leafColor: Color = PaperLeaf
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val scaleX = w / 100f
        val scaleY = h / 115f

        // Document background with rounded corners
        drawRoundRect(
            color = backgroundColor,
            topLeft = Offset(10f * scaleX, 8f * scaleY),
            size = Size(80f * scaleX, 100f * scaleY),
            cornerRadius = CornerRadius(10f * scaleX, 10f * scaleY)
        )

        // Folded top-right page corner
        val foldPath = Path().apply {
            moveTo(75f * scaleX, 8f * scaleY)
            lineTo(90f * scaleX, 8f * scaleY)
            lineTo(90f * scaleX, 23f * scaleY)
            close()
        }
        drawPath(foldPath, color = leafColor)

        // Shitol Pata (Leaf) shape in the center
        val leafPath = Path().apply {
            moveTo(50f * scaleX, 24f * scaleY)
            cubicTo(
                70f * scaleX, 38f * scaleY,
                70f * scaleX, 80f * scaleY,
                50f * scaleX, 96f * scaleY
            )
            cubicTo(
                30f * scaleX, 80f * scaleY,
                30f * scaleX, 38f * scaleY,
                50f * scaleX, 24f * scaleY
            )
            close()
        }
        drawPath(leafPath, color = leafColor)

        // Leaf central vein line
        drawLine(
            color = backgroundColor,
            start = Offset(50f * scaleX, 32f * scaleY),
            end = Offset(50f * scaleX, 88f * scaleY),
            strokeWidth = 2.5f * scaleX,
            cap = StrokeCap.Round
        )
    }
}
