package com.heckeck.aitextrec

import android.graphics.Bitmap

data class MainState(
    val selectedImageBitmap: Bitmap? = null,
    val isLoading: Boolean = false,
    val response: String = ""
)
