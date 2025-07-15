package cn.aicamera.frontend.ui.copywriting

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import cn.aicamera.frontend.MainActivity
import cn.aicamera.frontend.common.RouteConfig
import cn.aicamera.frontend.common.SelectMultiplePicture
import cn.aicamera.frontend.ui.camera.CameraActivity
import cn.aicamera.frontend.viewmodel.ChatViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@SuppressLint("PermissionLaunchedDuringComposition")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImageChooseScreen(navController: NavController, chatViewModel: ChatViewModel) {
    val context = LocalContext.current
    val showGallery = remember { mutableStateOf(false) }
    val showState = remember { mutableStateOf(false) }
    val galleryPermissionState =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)
        } else { // Android 12 及以下
            rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    if (galleryPermissionState.status.isGranted && !showState.value) {
        showGallery.value = true
    } else {
        galleryPermissionState.launchPermissionRequest()
    }
    OpenGallery(context, showGallery.value,
        onSelectPicture = { uris ->
            showGallery.value = false
            showState.value = true
            chatViewModel.clearImages()
            uris.forEach { uri ->
                chatViewModel.addImage(uri)
            }
            navController.navigate(RouteConfig.CHAT.toString()) {
                launchSingleTop = true
            }
        }, reloadGallery = {
            showGallery.value=false
            showGallery.value=true
        })
}

@Composable
private fun OpenGallery(
    context: Context,
    showGallery: Boolean,
    onSelectPicture: (List<Uri>) -> Unit,
    reloadGallery:()->Unit
) {
    if (showGallery) {
        var openGalleryLauncher: ManagedActivityResultLauncher<Unit?, List<Uri>?>? =
            rememberLauncherForActivityResult(contract = SelectMultiplePicture()) { uris ->
                if (uris != null && uris.size > 0 && uris.size < 10) {
                    onSelectPicture(uris)
                } else if (uris == null) { //用户选择退出
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "请选择1~9张图片", Toast.LENGTH_LONG).show()
                    reloadGallery()
                }
            }
        SideEffect {
            if (openGalleryLauncher != null) {
                openGalleryLauncher.launch(null)
            }
        }
    }
}