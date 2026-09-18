package com.example.shitolpata.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarComponent(
    currentScreen: String,
    hasActiveDocument: Boolean,
    activeDocumentName: String?,
    language: String,
    onNavigate: (String) -> Unit,
    onCloseDocument: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEn = language == "en"
    val isSubPage = currentScreen == "about" || currentScreen == "privacy"

    TopAppBar(
        modifier = modifier.testTag("top_bar"),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        ),
        navigationIcon = {
            if (isSubPage) {
                val backTestTag = if (currentScreen == "privacy") "privacy_back_button" else "about_back_button"
                IconButton(
                    onClick = { onNavigate("home") },
                    modifier = Modifier.testTag(backTestTag)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = if (isEn) "Back" else "ফিরে যান"
                    )
                }
            } else if (hasActiveDocument) {
                // When document is loaded, allow tapping back/close to return to home
                IconButton(
                    onClick = onCloseDocument,
                    modifier = Modifier.testTag("close_document_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = if (isEn) "Close Document" else "ডকুমেন্ট বন্ধ করুন"
                    )
                }
            } else {
                // On clean Home page: NO top logo and NO top name (it's in the middle!)
                Spacer(modifier = Modifier.width(0.dp))
            }
        },
        title = {
            if (isSubPage) {
                Text(
                    text = when (currentScreen) {
                        "privacy" -> if (isEn) "Privacy Policy" else "গোপনীয়তা নীতিমালা"
                        else -> if (isEn) "About Shitol Pata" else "পরিচিতি"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp
                    )
                )
            } else if (hasActiveDocument && activeDocumentName != null) {
                Text(
                    text = activeDocumentName,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                // On clean Home page: empty title because brand name is centered in middle!
            }
        },
        actions = {
            // Small settings icon at top right
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .padding(end = 6.dp)
                    .size(40.dp)
                    .testTag("settings_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = if (isEn) "Settings" else "সেটিংস",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    )
}
