package com.example.digicamera.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.location.Location
import android.os.Build
import com.example.digicamera.data.model.ImageMetadata
import com.example.digicamera.data.repository.AttestationRepository
import com.example.digicamera.utils.GalleryUtils
import com.example.digicamera.utils.QRCodeGenerator
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class CaptureAndAttestImageUseCase @Inject constructor(
    private val attestationRepository: AttestationRepository,
    private val qrCodeGenerator: QRCodeGenerator,
    private val galleryUtils: GalleryUtils,
    @ApplicationContext private val context: Context
) {
    suspend fun execute(
        imageFile: File,
        location: Location?
    ): Result<String> {
        return try {
            // Create metadata
            val metadata = ImageMetadata(
                timestamp = System.currentTimeMillis(),
                latitude = location?.latitude,
                longitude = location?.longitude,
                deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}",
                appVersion = "1.0"
            )

            // Attest image with API
            val attestationResult = attestationRepository.attestImage(imageFile, metadata)

            if (attestationResult.isFailure) {
                return Result.failure(attestationResult.exceptionOrNull() ?: Exception("Attestation failed"))
            }

            val attestationResponse = attestationResult.getOrNull()!!

            // Generate QR code
            val qrBitmap = qrCodeGenerator.generateQRCode(attestationResponse.qrData)

            // Load original image
            val originalBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)

            // Combine image with QR code
            val finalBitmap = combineImageWithQR(originalBitmap, qrBitmap)

            // Save to gallery
            val savedUri = galleryUtils.saveImageToGallery(context, finalBitmap, "DigiVoter_${System.currentTimeMillis()}")

            if (savedUri != null) {
                Result.success("Image saved successfully with attestation QR code")
            } else {
                Result.failure(Exception("Failed to save image to gallery"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun combineImageWithQR(originalBitmap: Bitmap, qrBitmap: Bitmap): Bitmap {
        val combinedBitmap = Bitmap.createBitmap(
            originalBitmap.width,
            originalBitmap.height,
            originalBitmap.config ?: Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(combinedBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Draw original image
        canvas.drawBitmap(originalBitmap, 0f, 0f, paint)

        // Calculate QR position (bottom-right corner with padding)
        val qrSize = minOf(originalBitmap.width, originalBitmap.height) / 6
        val scaledQR = Bitmap.createScaledBitmap(qrBitmap, qrSize, qrSize, true)

        val padding = 20f
        val qrX = originalBitmap.width - qrSize - padding
        val qrY = originalBitmap.height - qrSize - padding

        // Draw QR code
        canvas.drawBitmap(scaledQR, qrX, qrY, paint)

        return combinedBitmap
    }
}
