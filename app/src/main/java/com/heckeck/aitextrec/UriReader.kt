package com.heckeck.aitextrec

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.IOException
import java.io.InputStream

class UriReader(private val context: Context) {

    fun readBitmap(uri: Uri, maxSizeInKb: Int = 1024): Bitmap {
        return if (Build.VERSION.SDK_INT < 28) {
            val originalBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            getResizedBitmap(originalBitmap, maxSizeInKb)
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            val originalBitmap = ImageDecoder.decodeBitmap(source)
            getResizedBitmap(originalBitmap, maxSizeInKb)
        }
    }

    private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }
}
