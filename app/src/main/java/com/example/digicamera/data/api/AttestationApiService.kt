package com.example.digicamera.data.api


import com.example.digicamera.data.model.AttestationResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface AttestationApiService {
    @Multipart
    @POST("attest-image")
    suspend fun attestImage(
        @Part image: MultipartBody.Part,
        @Part("timestamp") timestamp: RequestBody,
        @Part("latitude") latitude: RequestBody?,
        @Part("longitude") longitude: RequestBody?,
        @Part("device_model") deviceModel: RequestBody,
        @Part("app_version") appVersion: RequestBody
    ): Response<AttestationResponse>
}