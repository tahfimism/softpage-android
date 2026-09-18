package com.example.shitolpata.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header controls inside preview
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Split View Toggle Chip
                FilterChip(
                    selected = splitMode,
                    onClick = onToggleSplitMode,
                    label = {
                        Text(
                            text = if (isEnglish) "Split Comparison" else "তুলনামূলক ভিউ",
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Compare,
                            contentDescription = "Split Comparison",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LeafPrimary.copy(alpha = 0.2f),
                        selectedLabelColor = LeafPrimary
                    ),
                    modifier = Modifier.testTag("split_view_toggle")
                )

                // Auto Otsu shortcut button
                FilledTonalButton(
                    onClick = onAutoOtsu,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = SunlightSecondary.copy(alpha = 0.15f),
                        contentColor = SunlightSecondary
                    ),
                    modifier = Modifier
                        .height(32.dp)
                        .testTag("auto_otsu_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoFixHigh,
                        contentDescription = "Auto Cutoff",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isEnglish) "Auto Otsu" else "স্বয়ংক্রিয় ওতসু",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Preview Canvas Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 360.dp, max = 500.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
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
                        horizontalAlignment = Alignment.CenterVertically,
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

                // Loading Overlay
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f)),
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

            Spacer(modifier = Modifier.height(12.dp))

            // Page Navigation Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onPreviousPage,
                    enabled = currentPage > 1 && !isLoading,
                    modifier = Modifier.testTag("prev_page_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Page"
                    )
                }

                Text(
                    text = if (isEnglish) "Page $currentPage of $totalPages" else "পৃষ্ঠা $currentPage / $totalPages",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .testTag("page_indicator_text")
                )

                IconButton(
                    onClick = onNextPage,
                    enabled = currentPage < totalPages && !isLoading,
                    modifier = Modifier.testTag("next_page_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Page"
                    )
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

            // 4. Draw handle knob in the middle of divider
            drawCircle(
                color = Color.White,
                radius = 14.dp.toPx(),
                center = Offset(splitX, size.height / 2f)
            )
            drawCircle(
                color = LeafPrimary,
                radius = 10.dp.toPx(),
                center = Offset(splitX, size.height / 2f)
            )
        }

        // Labels: "Original" on left, "Recolored" on right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (isEnglish) "Original" else "আসল",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(LeafPrimary.copy(alpha = 0.85f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (isEnglish) "Recolored" else "শীতল পাতা",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
