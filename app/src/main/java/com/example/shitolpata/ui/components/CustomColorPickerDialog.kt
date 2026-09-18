package com.example.shitolpata.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.toColorInt
import com.example.shitolpata.ui.theme.LeafPrimary

@Composable
fun CustomColorPickerDialog(
    initialBgHex: String,
    initialFgHex: String,
    isEnglish: Boolean,
    onDismiss: () -> Unit,
    onSavePalette: (String, String, String) -> Unit
) {
    var bgHex by remember { mutableStateOf(initialBgHex) }
    var fgHex by remember { mutableStateOf(initialFgHex) }
    var paletteName by remember { mutableStateOf("") }

    val bgPresetList = listOf("#0F0F10", "#181A20", "#0D1B2A", "#1C2826", "#FFF7E6", "#000000")
    val fgPresetList = listOf("#E9E9EA", "#E0D7C6", "#E8A33D", "#90E0EF", "#52B788", "#FFFFFF")

    val previewBg = remember(bgHex) {
        try { Color(bgHex.toColorInt()) } catch (e: Exception) { Color(0xFF0F0F10) }
    }
    val previewFg = remember(fgHex) {
        try { Color(fgHex.toColorInt()) } catch (e: Exception) { Color(0xFFE9E9EA) }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("custom_color_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = if (isEnglish) "Create Custom Palette" else "কাস্টম প্যালেট তৈরি করুন",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                // Live Preview Swatch
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(previewBg)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isEnglish) "Aa Sample Reading Text" else "কখগ শীতল পাতা নমুনা",
                        color = previewFg,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Palette Name
                OutlinedTextField(
                    value = paletteName,
                    onValueChange = { paletteName = it },
                    label = { Text(if (isEnglish) "Palette Name" else "প্যালেটের নাম") },
                    placeholder = { Text(if (isEnglish) "e.g. Midnight Forest" else "যেমন: মিডনাইট ফরেস্ট") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_palette_name")
                )

                // Background Color
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (isEnglish) "Background Color (Hex)" else "ব্যাকগ্রাউন্ডের রঙ (Hex)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    OutlinedTextField(
                        value = bgHex,
                        onValueChange = { bgHex = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_bg_hex")
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (colorHex in bgPresetList) {
                            val c = try { Color(colorHex.toColorInt()) } catch (e: Exception) { Color.Black }
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(c)
                                    .border(if (bgHex.equals(colorHex, true)) 2.dp else 1.dp, if (bgHex.equals(colorHex, true)) LeafPrimary else Color.Gray, CircleShape)
                                    .clickable { bgHex = colorHex }
                            )
                        }
                    }
                }

                // Foreground Color
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (isEnglish) "Foreground / Text Color (Hex)" else "টেক্সটের রঙ (Hex)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    OutlinedTextField(
                        value = fgHex,
                        onValueChange = { fgHex = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_fg_hex")
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (colorHex in fgPresetList) {
                            val c = try { Color(colorHex.toColorInt()) } catch (e: Exception) { Color.White }
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(c)
                                    .border(if (fgHex.equals(colorHex, true)) 2.dp else 1.dp, if (fgHex.equals(colorHex, true)) LeafPrimary else Color.Gray, CircleShape)
                                    .clickable { fgHex = colorHex }
                            )
                        }
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(if (isEnglish) "Cancel" else "বাতিল")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val name = if (paletteName.isNotBlank()) paletteName else "Custom"
                            onSavePalette(name, bgHex, fgHex)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LeafPrimary),
                        modifier = Modifier.testTag("btn_save_custom_palette")
                    ) {
                        Text(if (isEnglish) "Save Palette" else "সংরক্ষণ করুন")
                    }
                }
            }
        }
    }
}
