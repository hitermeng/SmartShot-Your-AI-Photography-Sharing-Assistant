package cn.aicamera.frontend.ui.camera.screen

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import cn.aicamera.frontend.MainActivity
import cn.aicamera.frontend.R
import cn.aicamera.frontend.model.ImageResponse
import cn.aicamera.frontend.ui.camera.PhotoPreviewScreen
import cn.aicamera.frontend.viewmodel.CameraViewModel
import kotlinx.coroutines.delay
import java.io.File
import java.util.Timer
import kotlin.concurrent.fixedRateTimer
import kotlin.math.max
import kotlin.math.min

/**
 * 相机界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraContent(
    cameraViewModel: CameraViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    var camera: Camera? = null // 记录绑定的相机变量

    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) } //默认选择后置相机

    // 相机缩放状态
    var scale by remember { mutableStateOf(1f) }
    val maxScale = 5f //最大缩放倍率

    val imageCapture = remember { ImageCapture.Builder().build() } // 拍照功能
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showPhotoPreview by remember { mutableStateOf(false) } // 记录是否完成一次拍照，跳转到保存页面
    var isUploading by remember { mutableStateOf(false) } // 照片是否需要上传,初始化相机完成后开始上传

    // 聚焦框
    var focusPoint by remember { mutableStateOf<Offset?>(null) } // 焦点位置
    var showFocusIndicator by remember { mutableStateOf(false) } // 框控制
    val focusIndicatorAlpha by animateFloatAsState(
        targetValue = if (showFocusIndicator) 1f else 0f,
        animationSpec = tween(durationMillis = 300)
    )
    val isVisible = remember { mutableStateOf(true) }
    val imageResponse = remember {
        mutableStateOf(
            ImageResponse(
                grade = -1,
                moveLeftRight = 0,
                moveUpDown = 0,
                moveForwardBackward = 0
            )
        )
    }
    var timer: Timer? = null
    LaunchedEffect(showFocusIndicator) {
        if (showFocusIndicator) {
            delay(1000) // 1秒后消失
            showFocusIndicator = false
        }
        // 定时任务，每隔五秒捕捉一次图像
        timer = fixedRateTimer(period = 5000) {
            if (!isUploading) {
                val file = File(context.externalCacheDir, "${System.currentTimeMillis()}.jpg")
                val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

                imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            isUploading = true
                            cameraViewModel.uploadImage(file, onSuccess = { res ->
                                isUploading = false
                                imageResponse.value = res
                                isVisible.value = true
                            }, { message ->
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                isUploading = false
                            })
                        }

                        override fun onError(exception: ImageCaptureException) {
                            Log.e("CameraScreen", "图片捕获失败: ${exception.message}")
                        }
                    }
                )
            }
        }
    }



    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
//                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
//                ),
                title = {
                    if (imageResponse.value.grade != null && imageResponse.value.grade >= 0) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
//                            Text(
//                                text = imageResponse.value.grade.toString(),
//                                fontSize = 24.sp,
//                                fontWeight = FontWeight.Bold,
//                                textAlign = TextAlign.Center
//                            )
                            Text(
                                text = imageResponse.value.grade.toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF242424)
                            )
                            Text(
                                text = "AI实时评分-${getStringByGrade(imageResponse.value.grade)}",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                },
                navigationIcon = {
                    IconButton(onClick = {
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = { // 为了使标题居中，需要一个无用的按钮在这里占位
                    IconButton(
                        enabled = false,
                        onClick = {
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            Modifier.alpha(0f)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 相机预览区域
            Box(
                modifier = Modifier
                    .height(screenHeight * 0.5f)
                    .fillMaxWidth()
                    .weight(1f)
                    .clipToBounds()
                    // 手势设置
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            // 监听双指缩放，进行画面缩放
                            scale = max(1f, min(scale * zoom, maxScale)) // 限制缩放范围
//                            scale = max(0f, min(scale + (zoom - 1) * 0.1f, 1f))
                            camera?.cameraControl?.setZoomRatio(scale)
//                            camera?.cameraControl?.setLinearZoom(scale)
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures { tapOffset ->
                            // 监听点击，进行聚焦
                            val factory = (context as? ComponentActivity)?.let { // 获取绑定了的相机焦点
                                (it.findViewById<PreviewView>(R.id.preview_view)).meteringPointFactory
                            }
                            factory?.let { pointFactory ->
                                // 创建聚焦操作
                                val action = FocusMeteringAction
                                    .Builder(
                                        pointFactory.createPoint(tapOffset.x, tapOffset.y)
                                    )
                                    .build()
                                camera?.cameraControl?.startFocusAndMetering(action)

                                // 显示聚焦框
                                focusPoint = tapOffset
                                showFocusIndicator = true
                            }
                        }
                    }
            ) {
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            id = R.id.preview_view
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            transformOrigin = TransformOrigin(0f, 0f), // 缩放锚点
                            compositingStrategy = CompositingStrategy.Auto // 硬件加速
                        ),
                    update = { previewView ->
                        camera = bindCameraView(
                            context, previewView, lifecycleOwner, cameraSelector, imageCapture
                        )
                    }
                )
//                FocusCanvas(focusPoint, focusIndicatorAlpha) // 聚焦框
                ArrowAnimation(
                    isVisible = isVisible.value,
                    imageResponse.value,
                    duration = 2000, // 毫秒
                    DisableVisible = {
                        isVisible.value = false
                    }
                )

            }
            FunctionArea({
                scale = max(1f, min(scale + it, 5f))
                camera?.cameraControl?.setZoomRatio(scale)
            }, {
                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                    cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                else
                    cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            }, {
                takePhoto(imageCapture, context) { bitmap ->
                    capturedBitmap = bitmap
                    isUploading = true
                    timer?.cancel()
                    showPhotoPreview = true
                }
            }, scale)
        }
    }


    if (showPhotoPreview && capturedBitmap != null) { // 下方窗口的渲染条件，如果条件不满足，会回到该页面
        PhotoPreviewScreen(
            bitmap = capturedBitmap!!,
            onDismiss = { showPhotoPreview = false }
        )
    }
}

// 提取出这个方法是为了调动cameraSelector的响应
// 在update时，可能由于addListener的原因，不糊响应cameraSelector的变化（仅单独提取出try catch无用）
private fun bindCameraView(
    context: Context,
    previewView: PreviewView,
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    imageCapture: ImageCapture
): Camera? {
    var camera: Camera? = null
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        // 设置相机预览，是图像的预览界面，而非UI的预览
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        try {
            // 绑定相机生命周期
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }, ContextCompat.getMainExecutor(context))
    return camera
}

private fun takePhoto(
    originImageCapture: ImageCapture,
    context: Context,
    onPhotoCaptured: (Bitmap) -> Unit
) {
    val imageCapture = originImageCapture ?: return
    // 临时照片文件
    val photoFile = File.createTempFile("photo_", ".jpg", context.cacheDir)
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    // 拍照
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                // 将照片文件转换为Bitmap
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                onPhotoCaptured(bitmap) // 传递给回调函数
                photoFile.delete() // 删除临时文件
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraScreen", "Failed to take photo", exception)
            }
        }
    )
}

@Composable
private fun getStringByGrade(grade: Int): String {
    var id = 0
    when {
        grade >= 90 -> id = R.string.grade_perfect
        grade >= 75 -> id = R.string.grade_great
        grade >= 60 -> id = R.string.grade_good
        grade >= 30 -> id = R.string.grade_bad
        else -> id = R.string.grade_worst
    }
    return stringResource(id)
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun Test(){
    FunctionArea({
    }, {
    }, {
    }, 0.0f)
}

