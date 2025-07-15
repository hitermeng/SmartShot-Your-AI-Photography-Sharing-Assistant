package cn.aicamera.frontend.network.service

import cn.aicamera.frontend.model.GeneralResponse
import cn.aicamera.frontend.model.LoginRequest
import cn.aicamera.frontend.model.RegisterRequest
import cn.aicamera.frontend.model.SuccessResponse
import cn.aicamera.frontend.model.UserProfile
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface UserService {
    // 用户注册
    @POST("/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<SuccessResponse>

    // 用户登录
    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<GeneralResponse<String>>

    // 退出登录
    @GET("/auth/logout")
    suspend fun logout(): Response<SuccessResponse>

    // 检查用户登录信息
    @GET("/auth/validate-login")
    suspend fun validateLogin(): Response<GeneralResponse<String>>

    // 获取用户信息
    @GET("/user/info")
    suspend fun getUserProfile(): Response<GeneralResponse<UserProfile>>

    @GET("/user/avatar")
    suspend fun getAvatar(@Query("avatarUrl") avatarUrl: String): Response<ResponseBody>

    // 更新用户信息，返回新用户信息
    @PUT("/user/update")
    suspend fun updateUserProfile(@Body request: UserProfile): Response<GeneralResponse<UserProfile>>

    // 上传头像，返回头像链接
    @Multipart
    @POST("/user/upload-avatar")
    suspend fun uploadAvatar(@Part avatar: MultipartBody.Part): Response<GeneralResponse<String>>
}