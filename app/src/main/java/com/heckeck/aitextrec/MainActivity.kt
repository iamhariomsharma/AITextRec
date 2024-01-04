package com.heckeck.aitextrec

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.ai.client.generativeai.GenerativeModel
import com.heckeck.aitextrec.ui.theme.AITextRecTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro-vision",
            apiKey = BuildConfig.apiKey,
        )
        val uriReader = UriReader(applicationContext)
        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            AITextRecTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) }
                    ) { paddingValues ->
                        val viewModel = viewModel<MainViewModel>(
                            factory = MainViewModelFactory(uriReader)
                        )
                        MainScreen(
                            state = viewModel.state,
                            snackbarHostState = snackbarHostState,
                            onPhotosPicked = viewModel::onImageSelected,
                            onSuggestionClick = {
                                viewModel.onSuggestClick(generativeModel)
                            },
                            onClear = viewModel::onClearImage,
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                }
            }
        }
    }
}