package com.example.tasky.agenda.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.tasky.R
import com.example.tasky.agenda.domain.model.Photo
import com.example.tasky.auth.presentation.showToast
import java.io.ByteArrayOutputStream
import java.io.InputStream

fun Bitmap.getByteArray(compress: Boolean = false): ByteArray {
    val stream = ByteArrayOutputStream()

    val compressionQuality = if (compress) 100 else 25
    compress(Bitmap.CompressFormat.JPEG, compressionQuality, stream)
    return stream.toByteArray()
}

fun ByteArray.isGreaterThan1MB() = size > 1024 * 1024

fun Context.getPhotoByteArrayPair(photo: Photo): Pair<String, ByteArray>? {
    val imageStream: InputStream? = contentResolver.openInputStream(Uri.parse(photo.url))
    val bitmap = BitmapFactory.decodeStream(imageStream)

    var byteArray = bitmap.getByteArray()
    if (byteArray.isGreaterThan1MB()) {
        byteArray = bitmap.getByteArray(compress = true)
    }

    return if (byteArray.isGreaterThan1MB()) {
        // return null for byte arrays greater than 1MB even after compression
        showToast(R.string.photo_too_large)
        null
    } else {
        photo.key to byteArray
    }
}