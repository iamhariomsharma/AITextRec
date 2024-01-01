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
import kotlinx.coroutines.launch
import java.io.IOException

class MainViewModel(
    private val uriReader: UriReader,
) : ViewModel() {

    var state by mutableStateOf(MainState())
        private set

    fun onImageSelected(uri: Uri) {
        viewModelScope.launch {
            try {
                state = state.copy(
                    selectedImageBitmap = uriReader.readBitmap(uri)
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun onClearImage() {
        state = state.copy(
            selectedImageBitmap = null,
            response = ""
        )
    }

    fun onSuggestClick(model: GenerativeModel) {
        if (state.selectedImageBitmap == null || state.isLoading) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            state = state.copy(isLoading = true)
            val response = model.generateContent(
                content {
                    image(state.selectedImageBitmap ?: return@content)
                    text("Please read document number only from this document without any space or dash, trim the space between or outside characters in your response if there is any, also capitalize all characters")
                }
            )
            println(response)
            state = state.copy(
                response = response.text ?: "Something went wrong",
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