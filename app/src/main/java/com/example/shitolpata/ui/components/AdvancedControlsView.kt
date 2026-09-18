package com.example.shitolpata.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shitolpata.model.AdvancedSettings
import com.example.shitolpata.model.Settings
import com.example.shitolpata.ui.theme.LeafPrimary
import com.example.shitolpata.ui.theme.SunlightSecondary

@Composable
fun AdvancedControlsView(
    settings: Settings,
    isEnglish: Boolean,
    onUpdateSettings: ((Settings) -> Settings) -> Unit,
    onAutoOtsu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .testTag("advanced_controls_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (isEnglish) "Fine-Tuning & Engine Controls" else "অ্যাডজাস্টমেন্ট এবং ইঞ্জিন সেটিংস",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Switch: Inverted document
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isEnglish) "Already Inverted (Dark PDF)" else "ইতিমধ্যে ডার্ক মোড পিডিএফ",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = if (isEnglish) "Enable if the source document already has a dark background" else "মূল পিডিএফ যদি ইতিমধ্যে কালো বা ডার্ক ব্যাকগ্রাউন্ডের হয়",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = settings.alreadyInverted,
                    onCheckedChange = { checked ->
                        onUpdateSettings { it.copy(alreadyInverted = checked) }
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = LeafPrimary),
                    modifier = Modifier.testTag("switch_already_inverted")
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // Switch: Preserve Images
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isEnglish) "Preserve Photos & Graphics" else "ছবি ও গ্রাফিক্স অক্ষুণ্ণ রাখুন",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = if (isEnglish) "Retains chromatic saturation for illustrations while recoloring text" else "পাঠ্য রিকালার করার সাথে সাথে রঙিন ডায়াগ্রাম ও ছবি স্বাভাবিক রাখে",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = settings.preserveImages,
                    onCheckedChange = { checked ->
                        onUpdateSettings { it.copy(preserveImages = checked) }
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = LeafPrimary),
                    modifier = Modifier.testTag("switch_preserve_images")
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // Threshold Slider + Auto Otsu Button
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEnglish) "Threshold Cutoff: ${settings.advanced.manualThreshold}"
                        else "থ্রেশহোল্ড কাট-অফ: ${settings.advanced.manualThreshold}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                    TextButton(
                        onClick = onAutoOtsu,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.testTag("btn_auto_otsu")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoFixHigh,
                            contentDescription = "Auto",
                            tint = SunlightSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isEnglish) "Auto Otsu" else "স্বয়ংক্রিয় ওতসু",
                            color = SunlightSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Slider(
                    value = settings.advanced.manualThreshold.toFloat(),
                    onValueChange = { value ->
                        onUpdateSettings {
                            it.copy(advanced = it.advanced.copy(manualThreshold = value.toInt()))
                        }
                    },
                    valueRange = 100f..250f,
                    colors = SliderDefaults.colors(thumbColor = LeafPrimary, activeTrackColor = LeafPrimary),
                    modifier = Modifier.testTag("slider_threshold")
                )
            }

            // Brightness Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEnglish) "Brightness: ${settings.advanced.brightness}"
                        else "উজ্জ্বলতা: ${settings.advanced.brightness}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                    if (settings.advanced.brightness != 0) {
                        IconButton(
                            onClick = {
                                onUpdateSettings { it.copy(advanced = it.advanced.copy(brightness = 0)) }
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(16.dp))
                        }
                    }
                }
                Slider(
                    value = settings.advanced.brightness.toFloat(),
                    onValueChange = { value ->
                        onUpdateSettings {
                            it.copy(advanced = it.advanced.copy(brightness = value.toInt()))
                        }
                    },
                    valueRange = -50f..50f,
                    colors = SliderDefaults.colors(thumbColor = LeafPrimary, activeTrackColor = LeafPrimary),
                    modifier = Modifier.testTag("slider_brightness")
                )
            }

            // Contrast Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEnglish) String.format("Contrast: %.2fx", settings.advanced.contrast)
                        else String.format("কনট্রাস্ট: %.2fx", settings.advanced.contrast),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                    if (settings.advanced.contrast != 1.0f) {
                        IconButton(
                            onClick = {
                                onUpdateSettings { it.copy(advanced = it.advanced.copy(contrast = 1.0f)) }
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(16.dp))
                        }
                    }
                }
                Slider(
                    value = settings.advanced.contrast,
                    onValueChange = { value ->
                        onUpdateSettings {
                            it.copy(advanced = it.advanced.copy(contrast = value))
                        }
                    },
                    valueRange = 0.5f..1.8f,
                    colors = SliderDefaults.colors(thumbColor = LeafPrimary, activeTrackColor = LeafPrimary),
                    modifier = Modifier.testTag("slider_contrast")
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // DPI Selector
            Column {
                Text(
                    text = if (isEnglish) "Rendering Quality (DPI)" else "রেন্ডারিং কোয়ালিটি (ডিপিআই)",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val dpis = listOf(72 to "72 (Fast)", 150 to "150 (Default)", 192 to "192 (Sharp)", 300 to "300 (Print)")
                    for ((dpiVal, label) in dpis) {
                        val isSelected = settings.dpi == dpiVal
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                onUpdateSettings { it.copy(dpi = dpiVal) }
                            },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LeafPrimary.copy(alpha = 0.2f),
                                selectedLabelColor = LeafPrimary
                            ),
                            modifier = Modifier.weight(1f).testTag("dpi_chip_$dpiVal")
                        )
                    }
                }
            }
        }
    }
}
