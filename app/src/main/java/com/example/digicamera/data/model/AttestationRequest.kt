package com.example.digicamera.data.model


import com.google.gson.annotations.SerializedName

data class AttestationRequest(
    @SerializedName("image_data")
    val imageData: String, // Base64 encoded image

    @SerializedName("metadata")
    val metadata: ImageMetadata
)

data class AttestationResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("attestation_id")
    val attestationId: String,

    @SerializedName("qr_data")
    val qrData: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("timestamp")
    val timestamp: Long? = null,

    @SerializedName("signature")
    val signature: String? = null
)

data class ImageMetadata(
    @SerializedName("timestamp")
    val timestamp: Long,

    @SerializedName("latitude")
    val latitude: Double?,

    @SerializedName("longitude")
    val longitude: Double?,

    @SerializedName("device_model")
    val deviceModel: String,

    @SerializedName("app_version")
    val appVersion: String,

    @SerializedName("device_id")
    val deviceId: String,

    @SerializedName("camera_info")
    val cameraInfo: CameraInfo? = null
)

data class CameraInfo(
    @SerializedName("resolution")
    val resolution: String,

    @SerializedName("camera_type")
    val cameraType: String, // "back" or "front"

    @SerializedName("flash_used")
    val flashUsed: Boolean = false
)

// Error response model
data class ApiErrorResponse(
    @SerializedName("error")
    val error: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("code")
    val code: Int? = null
)
