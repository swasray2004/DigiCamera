package com.example.digicamera.data.model

import okhttp3.MultipartBody
import okhttp3.RequestBody

data class AttestationRequest(
    val image: MultipartBody.Part,
    val timestamp: RequestBody,
    val location: RequestBody,
    val deviceInfo: RequestBody
)

data class AttestationResponse(
    val success: Boolean,
    val attestationId: String,
    val qrData: String,
    val message: String
)

data class ImageMetadata(
    val timestamp: Long,
    val latitude: Double?,
    val longitude: Double?,
    val deviceModel: String,
    val appVersion: String
)