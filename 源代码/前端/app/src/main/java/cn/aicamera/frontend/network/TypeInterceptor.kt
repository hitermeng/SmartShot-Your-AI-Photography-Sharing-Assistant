package cn.aicamera.frontend.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TypeInterceptor  @Inject constructor(): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val request = originalRequest.newBuilder()
            .header("Content-Type", "application/json")
//            .header("Accept", "application/json")
            .build()
        return chain.proceed(request)
    }
}