package com.example.shitolpata.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shitolpata.ui.theme.LeafPrimary

@Composable
fun PrivacyScreen(
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
            .testTag("privacy_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Shield",
                        tint = LeafPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                    Column {
                        Text(
                            text = if (isEnglish) "Your Documents Never Leave This Device" else "আপনার ডকুমেন্টস সম্পূর্ণ ব্যক্তিগত",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (isEnglish) "Air-gapped by design. No tracking." else "কোনো ক্লাউড বা ট্র্যাকিং নেই।",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                PrivacyCheckItem(
                    title = if (isEnglish) "Zero Cloud Transmission" else "কোনো দূরবর্তী সার্ভারে প্রেরণ নেই",
                    detail = if (isEnglish)
                        "When you select or open a PDF in Shitol Pata, the file is read and recolored using on-device graphics processing. No bytes are sent across the network."
                    else
                        "আপনার পিডিএফ শুধুমাত্র আপনার ডিভাইসের ইন্টারনাল মেমরিতে প্রসেস হয়। ইন্টারনেটে কোনো ডেটা আদান-প্রদান করা হয় না।"
                )

                PrivacyCheckItem(
                    title = if (isEnglish) "No Account or Login Required" else "কোনো অ্যাকাউন্ট বা লগইন প্রয়োজন নেই",
                    detail = if (isEnglish)
                        "You don't need to sign up, link an email, or share personal identifying information. Start recoloring immediately."
                    else
                        "কোনো সাইন-আপ বা ব্যক্তিগত তথ্য ছাড়াই অ্যাপটি তাৎক্ষণিকভাবে ব্যবহারযোগ্য।"
                )

                PrivacyCheckItem(
                    title = if (isEnglish) "Works Completely Offline" else "সম্পূর্ণ অফলাইনে কার্যকর",
                    detail = if (isEnglish)
                        "Turn on Airplane Mode anytime. All PDF recoloring, page rendering, threshold adjustments, and export features operate 100% offline."
                    else
                        "ইন্টারনেট সংযোগ বন্ধ থাকলেও অ্যাপের সকল সুবিধা পুরোপুরি কার্যকর থাকে।"
                )

                PrivacyCheckItem(
                    title = if (isEnglish) "No Analytics or Ad Trackers" else "কোনো বিজ্ঞাপন বা ট্র্যাকার নেই",
                    detail = if (isEnglish)
                        "The application contains zero telemetry trackers, third-party advertising SDKs, or behavioral profiling libraries."
                    else
                        "অ্যাপে কোনো ট্র্যাকিং কোড বা বিজ্ঞাপনী উপাদান অন্তর্ভুক্ত নেই।"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = LeafPrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEnglish) "Back to Recolor Studio" else "স্টুডিওতে ফিরুন")
        }
    }
}

@Composable
fun PrivacyCheckItem(title: String, detail: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Check",
            tint = LeafPrimary,
            modifier = Modifier.size(20.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }
    }
}
