package com.example.shitolpata.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shitolpata.ui.theme.LeafPrimary
import com.example.shitolpata.ui.theme.SunlightSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    isDarkTheme: Boolean,
    language: String,
    onDismiss: () -> Unit,
    onToggleTheme: () -> Unit,
    onToggleLanguage: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenPrivacy: () -> Unit
) {
    val isEn = language == "en"

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = Modifier.testTag("settings_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            Text(
                text = if (isEn) "Settings" else "সেটিংস",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
            )

            // Theme Toggle Option
            SettingsRow(
                icon = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                iconTint = if (isDarkTheme) SunlightSecondary else LeafPrimary,
                title = if (isEn) "Appearance" else "থিম ও ডিসপ্লে",
                subtitle = if (isDarkTheme) (if (isEn) "Dark Mode" else "ডার্ক মোড") else (if (isEn) "Light Mode" else "লাইট মোড"),
                trailingContent = {
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { onToggleTheme() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SunlightSecondary,
                            checkedTrackColor = LeafPrimary
                        ),
                        modifier = Modifier.testTag("theme_toggle_button")
                    )
                },
                onClick = onToggleTheme
            )

            // Language Toggle Option
            SettingsRow(
                icon = Icons.Default.Translate,
                iconTint = LeafPrimary,
                title = if (isEn) "Language" else "ভাষা",
                subtitle = if (isEn) "English (Change to বাংলা)" else "বাংলা (Change to English)",
                trailingContent = {
                    FilledTonalButton(
                        onClick = onToggleLanguage,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("language_toggle_button")
                    ) {
                        Text(
                            text = if (isEn) "বাংলা" else "ENG",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                },
                onClick = onToggleLanguage
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // About Page Option
            SettingsRow(
                icon = Icons.Default.Info,
                iconTint = LeafPrimary,
                title = if (isEn) "About Shitol Pata" else "শীতল পাতা পরিচিতি",
                subtitle = if (isEn) "Philosophy & project details" else "দর্শন ও প্রকল্প বিবরণী",
                trailingContent = null,
                onClick = {
                    onDismiss()
                    onOpenAbout()
                },
                testTag = "settings_about_button"
            )

            // Privacy Policy Option
            SettingsRow(
                icon = Icons.Default.Lock,
                iconTint = LeafPrimary,
                title = if (isEn) "Privacy Policy" else "গোপনীয়তা নীতিমালা",
                subtitle = if (isEn) "100% on-device & private assurance" else "সম্পূর্ণ নিরাপদ ও ব্যক্তিগত",
                trailingContent = null,
                onClick = {
                    onDismiss()
                    onOpenPrivacy()
                },
                testTag = "settings_privacy_button"
            )
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    title: String,
    subtitle: String,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
    testTag: String? = null
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier
            .fillMaxWidth()
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (trailingContent != null) {
                trailingContent()
            }
        }
    }
}
