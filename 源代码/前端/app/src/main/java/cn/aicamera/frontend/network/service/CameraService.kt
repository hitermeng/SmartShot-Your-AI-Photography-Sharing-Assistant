package cn.aicamera.frontend.network.service

import cn.aicamera.frontend.model.GeneralResponse
import cn.aicamera.frontend.model.ImageResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CameraService {
    @Multipart
    @POST("/camera/upload")
    suspend fun uploadImage(@Part image: MultipartBody.Part): Response<GeneralResponse<ImageResponse>>
}