package com.example.piringku.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import java.io.File
import java.io.FileOutputStream

object ProfilePictureManager {
    private const val FILE_NAME = "profile_picture.jpg"

    private val _photoVersion = mutableStateOf(0)
    var photoVersion: Int
        get() = _photoVersion.value
        private set(value) { _photoVersion.value = value }

    fun getFile(context: Context): File {
        return File(context.filesDir, FILE_NAME)
    }

    fun getUri(context: Context): Uri? {
        val file = getFile(context)
        return if (file.exists()) Uri.fromFile(file) else null
    }

    fun exists(context: Context): Boolean {
        return getFile(context).exists()
    }

    fun save(context: Context, uri: Uri): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return false
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            if (bitmap == null) return false

            val outputStream = FileOutputStream(getFile(context))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.close()
            photoVersion++
            true
        } catch (_: Exception) {
            false
        }
    }

    fun saveBitmap(context: Context, bitmap: Bitmap): Boolean {
        return try {
            val outputStream = FileOutputStream(getFile(context))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.close()
            photoVersion++
            true
        } catch (_: Exception) {
            false
        }
    }

    fun delete(context: Context) {
        getFile(context).delete()
        photoVersion++
    }
}
