package com.example.digicamera.data.repository


import com.example.digicamera.data.api.AttestationApiService
import com.example.digicamera.data.model.AttestationResponse
import com.example.digicamera.data.model.ImageMetadata
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttestationRepository @Inject constructor(
    private val apiService: AttestationApiService
) {
    suspend fun attestImage(
        imageFile: File,
        metadata: ImageMetadata
    ): Result<AttestationResponse> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

            val timestampBody = metadata.timestamp.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val latitudeBody = metadata.latitude?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val longitudeBody = metadata.longitude?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val deviceModelBody = metadata.deviceModel.toRequestBody("text/plain".toMediaTypeOrNull())
            val appVersionBody = metadata.appVersion.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiService.attestImage(
                image = imagePart,
                timestamp = timestampBody,
                latitude = latitudeBody,
                longitude = longitudeBody,
                deviceModel = deviceModelBody,
                appVersion = appVersionBody
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("API call failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}