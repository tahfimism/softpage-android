package com.example.shitolpata.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shitolpata.ui.theme.LeafPrimary
import com.example.shitolpata.ui.theme.SunlightSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarComponent(
    currentScreen: String,
    language: String,
    isDarkTheme: Boolean,
    onNavigate: (String) -> Unit,
    onToggleLanguage: () -> Unit,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEn = language == "en"

    TopAppBar(
        modifier = modifier.testTag("top_bar"),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.clickable { onNavigate("home") }
            ) {
                // Leaf emblem icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(LeafPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Eco,
                        contentDescription = "Shitol Pata Logo",
                        tint = LeafPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = if (isEn) "Shitol Pata" else "শীতল পাতা",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        )
                    )
                    Text(
                        text = if (isEn) "PDF Recoloring" else "আই-ফ্রেন্ডলি রিডার",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        },
        actions = {
            // Navigation tabs
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { onNavigate("home") },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (currentScreen == "home") LeafPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_home_button")
                ) {
                    Text(
                        text = if (isEn) "Studio" else "স্টুডিও",
                        fontWeight = if (currentScreen == "home") FontWeight.Bold else FontWeight.Normal
                    )
                }

                TextButton(
                    onClick = { onNavigate("about") },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (currentScreen == "about") LeafPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_about_button")
                ) {
                    Text(
                        text = if (isEn) "About" else "পরিচিতি",
                        fontWeight = if (currentScreen == "about") FontWeight.Bold else FontWeight.Normal
                    )
                }

                TextButton(
                    onClick = { onNavigate("privacy") },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (currentScreen == "privacy") LeafPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_privacy_button")
                ) {
                    Text(
                        text = if (isEn) "Privacy" else "গোপনীয়তা",
                        fontWeight = if (currentScreen == "privacy") FontWeight.Bold else FontWeight.Normal
                    )
                }

                // Language toggle (EN / বাংলা)
                FilledTonalButton(
                    onClick = onToggleLanguage,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("language_toggle_button")
                ) {
                    Text(
                        text = if (isEn) "BN" else "EN",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Theme Toggle
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier.testTag("theme_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                        contentDescription = "Toggle Theme",
                        tint = SunlightSecondary
                    )
                }
            }
        }
    )
}
