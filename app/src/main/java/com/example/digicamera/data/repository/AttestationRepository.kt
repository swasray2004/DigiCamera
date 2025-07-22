package com.example.digicamera.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.Settings
import com.example.digicamera.data.api.AttestationApiService
import com.example.digicamera.data.model.AttestationRequest
import com.example.digicamera.data.model.AttestationResponse
import com.example.digicamera.data.model.ImageMetadata
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Base64

@Singleton
class AttestationRepository @Inject constructor(
    private val apiService: AttestationApiService
) {
    suspend fun attestImage(
        imageFile: File,
        metadata: ImageMetadata
    ): Result<AttestationResponse> {
        return try {
            // Convert image to base64
            val base64Image = convertImageToBase64(imageFile)

            // Create request payload
            val request = AttestationRequest(
                imageData = base64Image,
                metadata = metadata
            )

            // Make API call
            val response = apiService.attestImage(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("API call failed: ${response.code()} - $errorMessage"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun convertImageToBase64(imageFile: File): String {
        return try {
            // Load and compress image if needed
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            val compressedBitmap = compressImageIfNeeded(bitmap)

            // Convert to base64
            val byteArrayOutputStream = ByteArrayOutputStream()
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP)
        } catch (e: Exception) {
            throw Exception("Failed to convert image to base64: ${e.message}")
        }
    }

    private fun compressImageIfNeeded(bitmap: Bitmap): Bitmap {
        val maxWidth = 1920
        val maxHeight = 1080

        return if (bitmap.width > maxWidth || bitmap.height > maxHeight) {
            val ratio = minOf(
                maxWidth.toFloat() / bitmap.width,
                maxHeight.toFloat() / bitmap.height
            )

            val newWidth = (bitmap.width * ratio).toInt()
            val newHeight = (bitmap.height * ratio).toInt()

            Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        } else {
            bitmap
        }
    }
}