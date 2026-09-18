package com.example.shitolpata.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shitolpata.ui.theme.LeafPrimary
import com.example.shitolpata.ui.theme.SunlightSecondary

@Composable
fun PagePreviewComponent(
    originalBitmap: Bitmap?,
    recoloredBitmap: Bitmap?,
    currentPage: Int,
    totalPages: Int,
    isLoading: Boolean,
    splitMode: Boolean,
    splitSliderPos: Float,
    isEnglish: Boolean,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    onToggleSplitMode: () -> Unit,
    onSplitSliderChange: (Float) -> Unit,
    onAutoOtsu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("page_preview_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header chips: Split Comparison & Auto Otsu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = splitMode,
                    onClick = onToggleSplitMode,
                    label = {
                        Text(
                            text = if (isEnglish) "Compare" else "তুলনা",
                            fontSize = 12.sp,
                            fontWeight = if (splitMode) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Compare,
                            contentDescription = "Compare",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LeafPrimary.copy(alpha = 0.2f),
                        selectedLabelColor = LeafPrimary
                    ),
                    modifier = Modifier.testTag("split_view_toggle")
                )

                SuggestionChip(
                    onClick = onAutoOtsu,
                    label = {
                        Text(
                            text = if (isEnglish) "Auto Otsu" else "স্বয়ংক্রিয় ওতসু",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AutoFixHigh,
                            contentDescription = "Auto Cutoff",
                            tint = SunlightSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("auto_otsu_button")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Preview Canvas Area with phone-optimized aspect ratio
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 300.dp, max = 460.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        RoundedCornerShape(14.dp)
                    )
                    .testTag("preview_canvas_box"),
                contentAlignment = Alignment.Center
            ) {
                if (recoloredBitmap != null) {
                    if (splitMode && originalBitmap != null) {
                        // Split-view before/after comparison
                        SplitComparisonCanvas(
                            original = originalBitmap,
                            recolored = recoloredBitmap,
                            splitPos = splitSliderPos,
                            onSplitChange = onSplitSliderChange,
                            isEnglish = isEnglish,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Standard full recolored page view
                        androidx.compose.foundation.Image(
                            bitmap = recoloredBitmap.asImageBitmap(),
                            contentDescription = "Recolored PDF page",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    // Empty placeholder
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "No PDF",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isEnglish) "No document loaded" else "কোনো ডকুমেন্ট লোড করা হয়নি",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Loading Indicator Overlay
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.35f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = LeafPrimary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Phone-Optimized Floating Page Navigation Pill
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onPreviousPage,
                        enabled = currentPage > 1 && !isLoading,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("prev_page_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Page",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = if (isEnglish) "Page $currentPage of $totalPages" else "পৃষ্ঠা $currentPage / $totalPages",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .testTag("page_indicator_text")
                    )

                    IconButton(
                        onClick = onNextPage,
                        enabled = currentPage < totalPages && !isLoading,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("next_page_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Page",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SplitComparisonCanvas(
    original: Bitmap,
    recolored: Bitmap,
    splitPos: Float,
    onSplitChange: (Float) -> Unit,
    isEnglish: Boolean,
    modifier: Modifier = Modifier
) {
    var widthPx by remember { mutableFloatStateOf(1f) }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    val newPos = (change.position.x / widthPx).coerceIn(0.05f, 0.95f)
                    onSplitChange(newPos)
                }
            }
    ) {
        val origImg = remember(original) { original.asImageBitmap() }
        val recImg = remember(recolored) { recolored.asImageBitmap() }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("split_canvas")
        ) {
            widthPx = size.width
            val splitX = size.width * splitPos

            // 1. Draw recolored image over whole canvas
            drawImage(
                image = recImg,
                dstOffset = IntOffset.Zero,
                dstSize = IntSize(size.width.toInt(), size.height.toInt())
            )

            // 2. Clip left region and draw original image
            clipRect(left = 0f, top = 0f, right = splitX, bottom = size.height) {
                drawImage(
                    image = origImg,
                    dstOffset = IntOffset.Zero,
                    dstSize = IntSize(size.width.toInt(), size.height.toInt())
                )
            }

            // 3. Draw divider line
            drawLine(
                color = LeafPrimary,
                start = Offset(splitX, 0f),
                end = Offset(splitX, size.height),
                strokeWidth = 3.dp.toPx()
            )

            // 4. Draw handle indicator circle
            drawCircle(
                color = Color.White,
                radius = 12.dp.toPx(),
                center = Offset(splitX, size.height / 2)
            )
            drawCircle(
                color = LeafPrimary,
                radius = 9.dp.toPx(),
                center = Offset(splitX, size.height / 2)
            )
        }

        // Before / After labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.Black.copy(alpha = 0.6f)
            ) {
                Text(
                    text = if (isEnglish) "Original" else "মূল কপি",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = LeafPrimary.copy(alpha = 0.85f)
            ) {
                Text(
                    text = if (isEnglish) "Recolored" else "শীতল পাতা",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
