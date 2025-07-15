package cn.aicamera.frontend.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.aicamera.frontend.R
import cn.aicamera.frontend.model.LoginRequest
import cn.aicamera.frontend.model.RegisterRequest
import cn.aicamera.frontend.model.UserProfile
import cn.aicamera.frontend.network.TokenManager
import cn.aicamera.frontend.network.service.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val userService: UserService
) : ViewModel() {
    private val _user = MutableStateFlow(
        UserProfile(
            "",
            2,
            0,
            "",
            "",
            ""
        )
    )
    private val _avatar = MutableStateFlow<Bitmap?>(null)
    val user: StateFlow<UserProfile> = _user
    val avatar : StateFlow<Bitmap?> = _avatar

    fun getProfile(onSuccess: () -> Unit,onFailed: (String) -> Unit){
        viewModelScope.launch {
            val response = userService.getUserProfile()
            if(response.isSuccessful && response.body()!!.success && response.body()!!.response!=null){
                val res = response.body()!!.response
                val newUser = UserProfile(
                    username = res!!.username,
                    gender = res?.gender?:2,
                    age = res?.age?:0,
                    email = res.email,
                    preference = res?.preference?:"",
                    avatarUrl = res.avatarUrl?:null,
                )
                _user.value = newUser
                onSuccess()
            }
            else{
                response.body()?.let { onFailed(it.message) }
            }
        }
    }

    fun loadAvatar(context: Context, onFailed: (String) -> Unit) {
        viewModelScope.launch {
            val avatarUrl = _user.value.avatarUrl
            if(avatarUrl.isNullOrEmpty()){
                _avatar.value = BitmapFactory.decodeResource(context.resources,R.drawable.ic_default_avatar)
            }
            else{
                val response = userService.getAvatar(avatarUrl)
                if (response.isSuccessful) {
                    val inputStream = response.body()?.byteStream()
                    _avatar.value = BitmapFactory.decodeStream(inputStream)
                } else {
                    onFailed(response.message())
                }
            }
        }
    }
    fun updateProfile(
        username: String,
        gender: Int,
        age: Int,
        email: String,
        preference: String,
        onSuccess: () -> Unit, onFailed: (String) -> Unit
    ) {
        viewModelScope.launch {
            val response = userService.updateUserProfile(
                UserProfile(
                    username,
                    gender,
                    age,
                    email,
                    preference,
                    avatarUrl = null
                )
            )
            if (response.isSuccessful && response.body()!!.success) {
                _user.value = _user.value.copy(
                    username = username,
                    gender = gender,
                    age = age,
                    email = email,
                    preference = preference
                )
                onSuccess()
            } else {
                Log.e("Update Profile", "Update profile failed:${response.body()?.message}")
                onFailed(response.body()?.message ?: "信息上传失败，请联系管理员")
            }
        }
    }


    fun uploadAvatar(context: Context, uri: Uri, onSuccess: () -> Unit, onFailed: (String) -> Unit) {
        viewModelScope.launch {
            try {
                //  打开Uri对应的输入流
                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(uri)
                if (inputStream == null) {
                    onFailed("无法读取文件")
                    return@launch
                }

                // 创建一个临时文件
                val tempFile = File.createTempFile("avatar", ".jpg", context.cacheDir)
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }

                val requestBody = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("avatar", tempFile.name, requestBody)

                val response = userService.uploadAvatar(part)
                if (response.isSuccessful && response.body()!!.success) {
                    _user.value =
                        _user.value.copy(avatarUrl = response.body()?.response ?: _user.value.avatarUrl)
                    onSuccess()
                } else {
                    Log.e("Update Avatar", "Upload avatar failed:${response.body()?.message}")
                    onFailed(response.body()?.message ?: "头像上传失败，请联系管理员")
                }
            } catch (e: Exception) {
                Log.e("Update Avatar", "Upload avatar failed", e)
                onFailed("头像上传失败: ${e.message}")
            }
        }
    }

    fun register(
        email: String,
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onFailed: (String) -> Unit
    ){
        viewModelScope.launch {
            val registerRequest = RegisterRequest(email = email, username = username, password =  password)
            val response = userService.register(registerRequest)
            if (response.isSuccessful && response.body()!!.success) {
                onSuccess()
            } else if(response.isSuccessful){
                Log.e("Register", "Register failed:${response.body()!!.message}")
                onFailed(response.body()?.message ?: "邮箱已存在！")
            }
            else {
                Log.e("Register", "Register failed:${response.body()?.message}")
                onFailed(response.body()?.message ?: "注册失败，请检查网络或联系管理员")
            }
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit, onFailed: (String) -> Unit) {
        viewModelScope.launch {
            val loginRequest = LoginRequest(email, password)
            val response = userService.login(loginRequest)
            if (response.isSuccessful && response.body()!!.success && response.body()!!.response != null) {
                response.body()!!.response?.let {
                    tokenManager.clearToken()
                    tokenManager.saveToken(it)
                }
                onSuccess()
            }else if(response.isSuccessful) {
                Log.e("Log In", "Log in failed:${response.body()!!.message}")
                onFailed(response.body()?.message ?: "用户名或密码错误！")
            }
            else {
                Log.e("Log In", "Log in failed:${response.body()?.message}")
                onFailed(response.body()?.message ?: "登录失败，请检查网络或联系管理员")
            }
        }
    }

    fun validateLogin(onSuccess: () -> Unit,onFailed: (String) -> Unit){
        viewModelScope.launch {
            val response = userService.validateLogin()
            if (response.isSuccessful && response.body()!!.success && response.body()!!.response != null) {
                response.body()!!.response?.let {
                    tokenManager.clearToken()
                    tokenManager.saveToken(it)
                }
                onSuccess()
            }else if(response.isSuccessful) {
                Log.e("ValidateLogin", "Validate log in failed:${response.body()!!.message}")
                onFailed(response.body()?.message ?: "请重新登录")
            }
            else {
                Log.e("ValidateLogin", "Validate log in failed:${response.body()?.message}")
                onFailed(response.body()?.message ?: "验证登录失败，请检查网络或联系管理员")
            }
        }
    }
    fun logout(onSuccess: () -> Unit, onFailed: (String) -> Unit) {
        viewModelScope.launch {
            val response = userService.logout()
            if (response.isSuccessful && response.body()!!.success) {
                tokenManager.clearToken()
                onSuccess()
            } else {
                Log.e("Log Out", "Log out failed:${response.body()?.message}")
                onFailed(response.body()?.message ?: "退出登录失败，请检查网络或联系管理员")
            }
        }
    }
}