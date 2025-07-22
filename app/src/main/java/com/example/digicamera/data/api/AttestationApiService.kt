package com.example.digicamera.data.api

import com.example.digicamera.data.model.AttestationRequest
import com.example.digicamera.data.model.AttestationResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface AttestationApiService {

    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    @POST("attest-image")
    suspend fun attestImage(
        @Body request: AttestationRequest
    ): Response<AttestationResponse>

    // Alternative endpoint if you need different API structure
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    @POST("v1/attestation/create")
    suspend fun createAttestation(
        @Body request: AttestationRequest
    ): Response<AttestationResponse>
}

