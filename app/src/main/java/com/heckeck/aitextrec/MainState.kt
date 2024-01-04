package com.heckeck.aitextrec

import android.graphics.Bitmap

data class MainState(
    val selectedImageBitmaps: List<Bitmap> = emptyList(),
    val isLoading: Boolean = false,
    val response: String = ""
)
