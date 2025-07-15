package cn.aicamera.frontend.network

import cn.aicamera.frontend.network.service.CameraService
import cn.aicamera.frontend.network.service.ChatService
import cn.aicamera.frontend.network.service.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor,typeInterceptor: TypeInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(typeInterceptor)
            .connectTimeout(5, TimeUnit.SECONDS)  // 连接超时1，设置为较大的15秒
            .readTimeout(15, TimeUnit.SECONDS)     // 读取超时
            .writeTimeout(15, TimeUnit.SECONDS)    // 写入超时
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
//            .baseUrl("http://localhost:8080")
            .baseUrl("http://192.168.43.33:8080") // TODO: 修改为后端URL
            .client(okHttpClient) // 配置拦截器
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideChatService(retrofit: Retrofit): ChatService {
        return retrofit.create(ChatService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Provides
    @Singleton
    fun provideCameraService(retrofit: Retrofit): CameraService {
        return retrofit.create(CameraService::class.java)
    }
}