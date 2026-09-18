package com.example.shitolpata

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.shitolpata.ui.components.ProcessingDialog
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
            val toastMessage by viewModel.toastMessage.collectAsState()
            val isProcessing by viewModel.isProcessing.collectAsState()
            val processingPage by viewModel.processingPage.collectAsState()
            val processingTotal by viewModel.processingTotal.collectAsState()

            val snackbarHostState = remember { SnackbarHostState() }
            val isEnglish = language == "en"

            LaunchedEffect(toastMessage) {
                toastMessage?.let {
                    snackbarHostState.showSnackbar(it)
                    viewModel.clearToast()
                }
            }

            ShitolPataTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    contentWindowInsets = WindowInsets.systemBars,
                    topBar = {
                        TopBarComponent(
                            currentScreen = currentScreen,
                            language = language,
                            isDarkTheme = isDarkTheme,
                            onNavigate = { viewModel.navigate(it) },
                            onToggleLanguage = {
                                viewModel.setLanguage(if (language == "en") "bn" else "en")
                            },
                            onToggleTheme = { viewModel.toggleTheme() }
                        )
                    }
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
                                HomeScreen(viewModel = viewModel)
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
