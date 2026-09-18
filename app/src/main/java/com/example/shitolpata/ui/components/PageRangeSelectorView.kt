package com.example.shitolpata.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shitolpata.model.PageSelectionType
import com.example.shitolpata.model.Settings
import com.example.shitolpata.ui.theme.LeafPrimary

@Composable
fun PageRangeSelectorView(
    settings: Settings,
    totalPages: Int,
    isEnglish: Boolean,
    onUpdateSettings: ((Settings) -> Settings) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .testTag("page_range_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (isEnglish) "Pages to Process & Export" else "এক্সপোর্টের পৃষ্ঠা নির্বাচন",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Options: All, Range, Custom
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = settings.pagesSelection == PageSelectionType.ALL,
                    onClick = { onUpdateSettings { it.copy(pagesSelection = PageSelectionType.ALL) } },
                    label = { Text(if (isEnglish) "All Pages ($totalPages)" else "সব পৃষ্ঠা ($totalPages)", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LeafPrimary.copy(alpha = 0.2f),
                        selectedLabelColor = LeafPrimary
                    ),
                    modifier = Modifier.weight(1f).testTag("chip_pages_all")
                )

                FilterChip(
                    selected = settings.pagesSelection == PageSelectionType.RANGE,
                    onClick = { onUpdateSettings { it.copy(pagesSelection = PageSelectionType.RANGE) } },
                    label = { Text(if (isEnglish) "Range" else "রেঞ্জ", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LeafPrimary.copy(alpha = 0.2f),
                        selectedLabelColor = LeafPrimary
                    ),
                    modifier = Modifier.weight(1f).testTag("chip_pages_range")
                )

                FilterChip(
                    selected = settings.pagesSelection == PageSelectionType.CUSTOM,
                    onClick = { onUpdateSettings { it.copy(pagesSelection = PageSelectionType.CUSTOM) } },
                    label = { Text(if (isEnglish) "Custom" else "কাস্টম", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LeafPrimary.copy(alpha = 0.2f),
                        selectedLabelColor = LeafPrimary
                    ),
                    modifier = Modifier.weight(1f).testTag("chip_pages_custom")
                )
            }

            when (settings.pagesSelection) {
                PageSelectionType.RANGE -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = settings.pagesRangeStart.toString(),
                            onValueChange = { str ->
                                val v = str.toIntOrNull() ?: 1
                                onUpdateSettings { it.copy(pagesRangeStart = v.coerceIn(1, totalPages)) }
                            },
                            label = { Text(if (isEnglish) "Start Page" else "শুরু") },
                            modifier = Modifier.weight(1f).testTag("input_range_start"),
                            singleLine = true
                        )

                        Text("—", fontWeight = FontWeight.Bold)

                        OutlinedTextField(
                            value = settings.pagesRangeEnd.coerceAtLeast(settings.pagesRangeStart).toString(),
                            onValueChange = { str ->
                                val v = str.toIntOrNull() ?: totalPages
                                onUpdateSettings { it.copy(pagesRangeEnd = v.coerceIn(1, totalPages)) }
                            },
                            label = { Text(if (isEnglish) "End Page" else "শেষ") },
                            modifier = Modifier.weight(1f).testTag("input_range_end"),
                            singleLine = true
                        )
                    }
                }
                PageSelectionType.CUSTOM -> {
                    OutlinedTextField(
                        value = settings.pagesCustom,
                        onValueChange = { str ->
                            onUpdateSettings { it.copy(pagesCustom = str) }
                        },
                        placeholder = { Text(if (isEnglish) "e.g., 1, 3, 5-8" else "যেমন: ১, ৩, ৫-৮") },
                        label = { Text(if (isEnglish) "Custom Page Numbers" else "পৃষ্ঠার নম্বরসমূহ") },
                        modifier = Modifier.fillMaxWidth().testTag("input_custom_pages"),
                        singleLine = true
                    )
                }
                PageSelectionType.ALL -> {
                    Text(
                        text = if (isEnglish) "All $totalPages pages will be converted with high-precision local recoloring."
                        else "ডকুমেন্টের সকল $totalPages পৃষ্ঠা রূপান্তরিত ও এক্সপোর্ট করা হবে।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
