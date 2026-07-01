package com.example.piringku.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import java.io.File
import java.io.FileOutputStream

object ProfilePictureManager {
    private fun fileName(userId: Long) = "profile_${userId}.jpg"

    private val _photoVersion = mutableStateOf(0L)
    var photoVersion: Long
        get() = _photoVersion.value
        private set(value) { _photoVersion.value = value }

    fun getFile(context: Context, userId: Long): File {
        return File(context.filesDir, fileName(userId))
    }

    fun getUri(context: Context, userId: Long): Uri? {
        val file = getFile(context, userId)
        return if (file.exists()) Uri.fromFile(file) else null
    }

    fun exists(context: Context, userId: Long): Boolean {
        return getFile(context, userId).exists()
    }

    fun save(context: Context, uri: Uri, userId: Long): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return false
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            if (bitmap == null) return false

            val outputStream = FileOutputStream(getFile(context, userId))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.close()
            photoVersion++
            true
        } catch (_: Exception) {
            false
        }
    }

    fun saveBitmap(context: Context, bitmap: Bitmap, userId: Long): Boolean {
        return try {
            val outputStream = FileOutputStream(getFile(context, userId))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.close()
            photoVersion++
            true
        } catch (_: Exception) {
            false
        }
    }

    fun delete(context: Context, userId: Long) {
        getFile(context, userId).delete()
        photoVersion++
    }
}
