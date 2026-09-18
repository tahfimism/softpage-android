package com.example.shitolpata

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.shitolpata.ui.components.ProcessingDialog
import com.example.shitolpata.ui.components.SettingsBottomSheet
import com.example.shitolpata.ui.components.TopBarComponent
import com.example.shitolpata.ui.screens.AboutScreen
import com.example.shitolpata.ui.screens.HomeScreen
import com.example.shitolpata.ui.screens.PrivacyScreen
import com.example.shitolpata.ui.theme.ShitolPataTheme
import com.example.shitolpata.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle external PDF intent if opened from Downloads / Files app
        handleIntent(intent)

        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val language by viewModel.language.collectAsState()
            val files by viewModel.files.collectAsState()
            val activeIndex by viewModel.activeFileIndex.collectAsState()
            val toastMessage by viewModel.toastMessage.collectAsState()
            val isProcessing by viewModel.isProcessing.collectAsState()
            val processingPage by viewModel.processingPage.collectAsState()
            val processingTotal by viewModel.processingTotal.collectAsState()

            var showSettingsSheet by remember { mutableStateOf(false) }

            val snackbarHostState = remember { SnackbarHostState() }
            val isEnglish = language == "en"
            val activeDoc = files.getOrNull(activeIndex)

            LaunchedEffect(toastMessage) {
                toastMessage?.let {
                    snackbarHostState.showSnackbar(it)
                    viewModel.clearToast()
                }
            }

            ShitolPataTheme(darkTheme = isDarkTheme) {
                BackHandler(enabled = currentScreen != "home" || files.isNotEmpty()) {
                    if (currentScreen != "home") {
                        viewModel.navigate("home")
                    } else if (files.isNotEmpty()) {
                        viewModel.clearQueue()
                    }
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        TopBarComponent(
                            currentScreen = currentScreen,
                            hasActiveDocument = files.isNotEmpty(),
                            activeDocumentName = activeDoc?.name,
                            language = language,
                            onNavigate = { viewModel.navigate(it) },
                            onCloseDocument = { viewModel.clearQueue() },
                            onOpenSettings = { showSettingsSheet = true }
                        )
                    }
                    // Clean UX: NO bottom bar per user request
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                        when (currentScreen) {
                            "about" -> {
                                AboutScreen(
                                    isEnglish = isEnglish,
                                    onBack = { viewModel.navigate("home") }
                                )
                            }
                            "privacy" -> {
                                PrivacyScreen(
                                    isEnglish = isEnglish,
                                    onBack = { viewModel.navigate("home") }
                                )
                            }
                            else -> {
                                HomeScreen(
                                    viewModel = viewModel
                                )
                            }
                        }

                        // Modal Processing Dialog during PDF export
                        if (isProcessing) {
                            ProcessingDialog(
                                currentPage = processingPage,
                                totalPages = processingTotal,
                                isEnglish = isEnglish,
                                onCancel = { viewModel.cancelProcessing() }
                            )
                        }

                        // Clean Settings Bottom Sheet
                        if (showSettingsSheet) {
                            SettingsBottomSheet(
                                isDarkTheme = isDarkTheme,
                                language = language,
                                onDismiss = { showSettingsSheet = false },
                                onToggleTheme = { viewModel.toggleTheme() },
                                onToggleLanguage = {
                                    viewModel.setLanguage(if (language == "en") "bn" else "en")
                                },
                                onOpenAbout = { viewModel.navigate("about") },
                                onOpenPrivacy = { viewModel.navigate("privacy") }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return
        if (intent.action == Intent.ACTION_VIEW) {
            val uri = intent.data
            if (uri != null) {
                viewModel.loadPdfFromUri(this, uri)
            }
        }
    }
}
