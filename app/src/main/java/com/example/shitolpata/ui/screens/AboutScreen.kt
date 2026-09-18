package com.example.shitolpata.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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

@Composable
fun AboutScreen(
    isEnglish: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("about_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Back Action
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("about_back_button")) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = if (isEnglish) "About Shitol Pata" else "শীতল পাতা পরিচিতি",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        // Hero Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isEnglish) "Our Philosophy" else "আমাদের দর্শন",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = LeafPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = if (isEnglish)
                        "Gentle on the eyes, rooted in calm."
                    else
                        "ডিজিটাল ডকুমেন্টের জন্য এক টুকরো শীতল পাতা।",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    text = if (isEnglish)
                        "In Bengal, Shitol Pata (শীতল পাটি) refers to handcrafted mats woven from the murta plant, famous for providing a natural, soothing coolness on sweltering summer nights. We created Shitol Pata with the exact same aspiration: to bring comforting relief to students, researchers, and night owls reading harsh, blinding white PDF documents in dark rooms."
                    else
                        "বাংলার ঐতিহ্যবাহী শীতল পাটি যেমন গ্রীষ্মের উত্তপ্ত দিনে এনে দেয় প্রাকৃতিক স্নিগ্ধতা ও শীতল পরশ, ঠিক তেমনি ডিজিটাল মাধ্যমে দীর্ঘ সময় পড়ালেখায় চোখের ক্লান্তি দূর করতে আমাদের এই প্রয়াস। চোখের ওপর নীল আলোর চাপ কমিয়ে এনে দেয় প্রশান্তিময় পড়ার অভিজ্ঞতা।",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
            }
        }

        // Bento Grid: 3 Pillars
        Text(
            text = if (isEnglish) "Core Pillars" else "মূল বৈশিষ্ট্যাবলী",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        PillarCard(
            icon = Icons.Default.Visibility,
            title = if (isEnglish) "Designed for the Night Owls" else "রাতজাগা পাঠকদের জন্য",
            description = if (isEnglish)
                "Late-night study sessions with bright white scans cause eye fatigue, headaches, and disrupted sleep cycles. Shitol Pata shifts high-contrast harsh whites into warm sepia, deep midnight blues, or AMOLED black while keeping typography sharp and anti-aliased."
            else
                "গভীর রাতে উজ্জ্বল সাদা পাতার দিকে তাকিয়ে থাকলে চোখে তীব্র অস্বস্তি ও ঘুমহীনতা তৈরি হয়। শীতল পাতা ফন্ট ঠিক রেখে ডকুমেন্টকে করে তোলে শান্ত ও চোখ-বান্ধব।"
        )

        PillarCard(
            icon = Icons.Default.Lock,
            title = if (isEnglish) "100% Local & Private" else "শতভাগ নিরাপদ ও অফলাইন",
            description = if (isEnglish)
                "Zero cloud uploads. Zero remote servers. All Otsu contrast calculations, pixel binarization, and PDF generation happen directly inside your device's memory."
            else
                "আপনার কোনো ফাইল বা তথ্য ইন্টারনেটে পাঠানো হয় না। সকল রেন্ডারিং আপনার ডিভাইসের নিজস্ব প্রসেসরে সম্পূর্ণ অফলাইনে সম্পন্ন হয়।"
        )

        PillarCard(
            icon = Icons.Default.AutoFixHigh,
            title = if (isEnglish) "Smart Otsu Contrast Binarization" else "স্মার্ট ওতসু কনট্রাস্ট ইঞ্জিন",
            description = if (isEnglish)
                "Unlike naive inverted filters that turn images into negative x-rays, Shitol Pata uses statistical thresholding to map ink darkness smoothly while preserving charts, diagrams, and illustrations."
            else
                "সাধারণ ইনভার্টারের মতো ফটো নষ্ট না করে এটি গাণিতিক বিশ্লেষণের মাধ্যমে লেখার স্পষ্টতা ধরে রাখে এবং ছবি স্বাভাবিক রাখতে পারে।"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = LeafPrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEnglish) "Return to Workspace" else "স্টুডিওতে ফিরুন")
        }
    }
}

@Composable
fun PillarCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(LeafPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = LeafPrimary, modifier = Modifier.size(22.dp))
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
