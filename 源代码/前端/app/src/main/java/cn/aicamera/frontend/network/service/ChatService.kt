package cn.aicamera.frontend.network.service

import cn.aicamera.frontend.model.GeneralResponse
import cn.aicamera.frontend.model.MessageRequest
import cn.aicamera.frontend.model.SuccessResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Streaming


interface ChatService {
    // 发送文本消息
    @POST("/chat/send")
    suspend fun sendMessage(@Query("message")message: String) : Response<SuccessResponse>

    // 监听SSE消息流
    @GET("/chat/stream")
    @Streaming
    fun listenMessages(): Call<ResponseBody>

    // 上传图片
    @Multipart
    @POST("/chat/upload")
    suspend fun uploadImage(@Part image: MultipartBody.Part): Response<SuccessResponse>
}