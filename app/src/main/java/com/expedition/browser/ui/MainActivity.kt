package com.expedition.browser.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.expedition.browser.ui.screens.BrowserScreen
import com.expedition.browser.ui.theme.ExpeditionTheme
import com.expedition.browser.ui.viewmodel.BrowserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: BrowserViewModel = viewModel()
            val isDark by viewModel.isDarkTheme.collectAsState(initial = isSystemInDarkTheme())

            ExpeditionTheme(darkTheme = isDark) {
                BrowserScreen(viewModel = viewModel)
            }
        }
    }
}
