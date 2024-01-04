package com.heckeck.aitextrec

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.IOException

class MainViewModel(
    private val uriReader: UriReader,
) : ViewModel() {

    var state by mutableStateOf(MainState())
        private set

    fun onImageSelected(uris: List<Uri>) {
        viewModelScope.launch {
            try {
                val bitmaps = uris.map { uriReader.readBitmap(it) }
                state = state.copy(
                    selectedImageBitmaps = bitmaps
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun onClearImage() {
        state = state.copy(
            selectedImageBitmaps = emptyList(),
            response = ""
        )
    }

    fun onSuggestClick(model: GenerativeModel) {
        if (state.selectedImageBitmaps.isEmpty() || state.isLoading) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            state = state.copy(isLoading = true)

            val prompt = content {
                for (selectedImage in state.selectedImageBitmaps) {
                    image(selectedImage)
                    text("Please read document number only from these documents without any space or dash, trim the space between or outside characters in your response if there is any, also capitalize all characters")
                }
            }

            var output = ""

            model.generateContentStream(prompt).collect { chunk ->
                output += chunk.text
            }

            state = state.copy(
                response = output,
                isLoading = false
            )
        }
    }
}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
    private val uriReader: UriReader,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(uriReader) as T
    }
}