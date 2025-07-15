package cn.aicamera.frontend.ui.copywriting

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import cn.aicamera.frontend.common.RouteConfig
import cn.aicamera.frontend.ui.AppNavHost
import cn.aicamera.frontend.ui.theme.CameraAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CopywritingActivity : ComponentActivity() {
    private val selectedImageUris = mutableStateListOf<Uri>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imagePath: Uri? = intent.getParcelableExtra("image_path")
        setContent {
            CameraAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (imagePath == null) {
                        AppNavHost(RouteConfig.IMAGE_CHOOSE)
                    } else {
                        AppNavHost(RouteConfig.CHAT)
                    }
                }
            }
        }
    }
}

//    @Composable
//    private fun openGallery() {
//        var showGallery = remember { mutableStateOf(true) }
//        var openGalleryLauncher: ManagedActivityResultLauncher<Unit?, Uri?>? =
//            rememberLauncherForActivityResult(contract = SelectPicture()) { uri ->
//                if (uri != null) {
//                    onSelectImage(uri)
//                    showGallery.value=false
//                }
//                else {
//                    Toast.makeText(this, "请至少选择一张图片", Toast.LENGTH_LONG).show()
//                }
//            }
//        when {
//            galleryPermissionState.status.isGranted -> {
//                if(showGallery.value){
//                    SideEffect {
//                        if (openGalleryLauncher != null) {
//                            openGalleryLauncher.launch(null)
//                        }
//                    }
//                }
//            }
//
//            else -> {
//                galleryPermissionState.launchPermissionRequest()
//            }
//        }
//    }
//    private val pickImagesLauncher = registerForActivityResult(
//        ActivityResultContracts.OpenMultipleDocuments()
//    ) { uris: List<Uri>? ->
//        uris?.let {
//            selectedImageUris.clear()
//            selectedImageUris.addAll(it.take(9)) // 清空后，选择最多9张照片
//        }
//    }