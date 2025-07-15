package cn.aicamera.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.aicamera.frontend.model.ImageResponse
import cn.aicamera.frontend.network.service.CameraService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val cameraService: CameraService
) : ViewModel() {
    fun uploadImage(file: File,onSuccess:(ImageResponse)->Unit,onFailed:(String)->Unit) {
        viewModelScope.launch {
            try {
                val requestFile = file.asRequestBody("image/jpeg".toMediaType())
                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

                val response = cameraService.uploadImage(body)
                if (response.isSuccessful && response.body()?.success == true && response.body()?.response !=null) {
                    onSuccess(response.body()!!.response!!)
                } else if(response.isSuccessful){
                    onFailed("上传失败: ${response.body()?.message}")
                } else{
                    onFailed("上传失败: ${response.body()}")
                }
            } catch (e: Exception) {
                onFailed("上传失败: ${e.message}")
            }
        }
    }
}