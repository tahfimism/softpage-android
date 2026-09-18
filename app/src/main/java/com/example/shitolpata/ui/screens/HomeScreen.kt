package com.example.shitolpata.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shitolpata.model.Palette
import com.example.shitolpata.model.PdfDocumentItem
import com.example.shitolpata.model.Settings
import com.example.shitolpata.processing.DemoPdfType
import com.example.shitolpata.ui.components.*
import com.example.shitolpata.ui.theme.LeafPrimary
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
    val currentTab by viewModel.currentTab.collectAsState()
    val language by viewModel.language.collectAsState()
    val isEnglish = language == "en"

    var showCustomColorDialog by remember { mutableStateOf(false) }

    // SAF File Open Launcher (application/pdf)
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

    if (files.isEmpty()) {
        // Landing Page View
        LandingView(
            isEnglish = isEnglish,
            onPickPdf = { pdfPickerLauncher.launch(arrayOf("application/pdf")) },
            onSelectDemo = { type -> viewModel.loadDemoPdf(type) },
            modifier = modifier
        )
    } else {
        // Active Workspace View
        val activeDoc = files.getOrNull(activeIndex)

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
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                // Queue Bar (if document loaded)
                item {
                    DocumentQueueBar(
                        files = files,
                        activeIndex = activeIndex,
                        isEnglish = isEnglish,
                        onSelectFile = { viewModel.selectFile(it) },
                        onRemoveFile = { viewModel.removeFile(it) },
                        onAddMore = { pdfPickerLauncher.launch(arrayOf("application/pdf")) }
                    )
                }

                // Split Preview Component
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

                // Workspace Tabs: Design vs Adjustments
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
                                    text = if (isEnglish) "Color Schemes" else "রঙের স্কিম",
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

                // Tab Content
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

            // Fixed Bottom Export Action Bar
            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
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
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_save_page_image")
                    ) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isEnglish) "Save Page" else "ছবি সংরক্ষণ",
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = {
                            val defaultPdfName = "${activeDoc?.name?.substringBeforeLast(".") ?: "document"}_softpage.pdf"
                            pdfExportLauncher.launch(defaultPdfName)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LeafPrimary),
                        modifier = Modifier
                            .weight(1.6f)
                            .testTag("btn_export_recolored_pdf")
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isEnglish) "Export Recolored PDF" else "পিডিএফ এক্সপোর্ট করুন",
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

@Composable
fun LandingView(
    isEnglish: Boolean,
    onPickPdf: () -> Unit,
    onSelectDemo: (DemoPdfType) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("landing_view"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            // Hero Badge & Title
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(LeafPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = "Shitol Pata",
                    tint = LeafPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isEnglish) "Shitol Pata" else "শীতল পাতা",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
            )

            Text(
                text = if (isEnglish) "ডিজিটাল ডকুমেন্টের জন্য এক টুকরো শীতল পাতা" else "Digital document er jonno ek tukro shitol pata",
                style = MaterialTheme.typography.bodyMedium.copy(color = LeafPrimary),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isEnglish)
                    "Turn harsh, blinding PDFs into soft, eye-friendly documents for comfortable late-night reading."
                else
                    "চোখের ওপর তীব্র চাপ দূর করে আপনার পড়ার সময়কে করুন স্নিগ্ধ ও আরামদায়ক।",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        // Primary Call to Action: Open PDF
        item {
            Button(
                onClick = onPickPdf,
                colors = ButtonDefaults.buttonColors(containerColor = LeafPrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_choose_pdf")
            ) {
                Icon(imageVector = Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEnglish) "Choose PDF Document" else "পিডিএফ ফাইল সিলেক্ট করুন",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Instant Demo Buttons
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isEnglish) "Or Try a Sample Document" else "অথবা ডেমো ফাইল দিয়ে পরীক্ষা করুন",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onSelectDemo(DemoPdfType.MATH_WORKSHEET) },
                            modifier = Modifier.weight(1f).testTag("btn_demo_math"),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                        ) {
                            Text(if (isEnglish) "Math Sheet" else "গণিত", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { onSelectDemo(DemoPdfType.BOOK_CHAPTER) },
                            modifier = Modifier.weight(1f).testTag("btn_demo_book"),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                        ) {
                            Text(if (isEnglish) "Book Chapter" else "বইয়ের অধ্যায়", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { onSelectDemo(DemoPdfType.LEGAL_NDA) },
                            modifier = Modifier.weight(1f).testTag("btn_demo_nda"),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                        ) {
                            Text(if (isEnglish) "Legal NDA" else "চুক্তিপত্র", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Feature Highlights
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FeatureRow(
                    icon = Icons.Default.Lock,
                    title = if (isEnglish) "100% Local & Private" else "শতভাগ ব্যক্তিগত ও অন-ডিভাইস",
                    subtitle = if (isEnglish) "No uploads to external servers. Runs completely offline." else "কোনো ডেটা সার্ভারে যায় না। সম্পূর্ণ অফলাইনে কার্যকর।"
                )
                FeatureRow(
                    icon = Icons.Default.Palette,
                    title = if (isEnglish) "Curated Palettes & Presets" else "আই-ফ্রেন্ডলি রঙের প্যালেট",
                    subtitle = if (isEnglish) "Warm Sepia, Night Blue, Soft Gray, AMOLED, and custom hex codes." else "ওয়ার্ম সেপিয়া, নাইট ব্লু, অ্যামোলেড এবং নিজস্ব কাস্টম রঙ।"
                )
                FeatureRow(
                    icon = Icons.Default.AutoFixHigh,
                    title = if (isEnglish) "Statistical Otsu Binarization" else "স্মার্ট ওতসু থ্রেশহোল্ডিং",
                    subtitle = if (isEnglish) "Crisp anti-aliased font rendering with image preservation." else "ফন্ট স্পষ্ট রেখে ছবি ও ডায়াগ্রাম অক্ষুণ্ণ রাখে।"
                )
            }
        }
    }
}

@Composable
fun FeatureRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(LeafPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = LeafPrimary, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun DocumentQueueBar(
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
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEnglish) "Active Documents (${files.size})" else "ডকুমেন্ট তালিকা (${files.size})",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )

                TextButton(
                    onClick = onAddMore,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isEnglish) "Add File" else "নতুন যোগ", fontSize = 12.sp)
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(files) { index, doc ->
                    val isCurrent = index == activeIndex
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) LeafPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.5.dp, LeafPrimary) else null,
                        modifier = Modifier.clickable { onSelectFile(index) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
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
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${doc.pageCount} p • ${doc.formattedSize}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = { onRemoveFile(index) },
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
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
