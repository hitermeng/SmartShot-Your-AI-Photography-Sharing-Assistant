package cn.aicamera.frontend.ui.camera.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import cn.aicamera.frontend.R
import cn.aicamera.frontend.viewmodel.CameraViewModel


@Composable
fun CameraScreen(viewModel: CameraViewModel = hiltViewModel()) {
    val context = LocalContext.current

    // 状态：是否有相机权限
    var hasCameraPermission by remember {
        mutableStateOf(checkCameraPermission(context))
    }

    // 权限请求启动器
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasCameraPermission = isGranted
        }
    )

    // 如果没有权限，显示请求权限的 UI
    if (!hasCameraPermission) {
        PermissionRequestUI {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    } else {
        // 如果有权限，显示相机 UI
        CameraContent(viewModel)
    }
}

/**
 * 检查是否有相机权限
 */
private fun checkCameraPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}

/**
 * 请求权限的 UI
 */
@Composable
private fun PermissionRequestUI(onRequestPermission: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = buildBoldString(stringResource(R.string.camera_permission_required)),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRequestPermission,
                modifier = Modifier.height(100.dp)
            ) {
                Text(
                    text = stringResource(R.string.grant_permission),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    modifier = Modifier.wrapContentWidth()
                )
            }
        }
    }
}

private fun buildBoldString(fullText: String): AnnotatedString {
    return buildAnnotatedString {
        var currentIndex = 0
        var isBold = false
        while (currentIndex < fullText.length) {
            // <b>标签会被自动删除，所以要使用**
            if (fullText.startsWith("**", currentIndex)) {
                isBold = !isBold
                currentIndex += 2
            } else {
                // 添加文字
                if (isBold) {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(fullText[currentIndex].toString())
                    }
                } else {
                    append(fullText[currentIndex].toString())
                }
                currentIndex++
            }
        }
    }
}
@androidx.compose.ui.tooling.preview.Preview
@Composable
fun TestFuntionArea() {

}