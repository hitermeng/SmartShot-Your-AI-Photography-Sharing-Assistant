package cn.aicamera.frontend.ui.camera.screen

import android.annotation.SuppressLint
import androidx.annotation.ColorRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.aicamera.frontend.R
import cn.aicamera.frontend.ui.theme.Purple40
import cn.aicamera.frontend.ui.theme.Purple80

/**
 * 功能区域,包括拍照，调节缩放倍数等
 */
@SuppressLint("DefaultLocale")
@Composable
fun FunctionArea(
    updateScale: (Float) -> Unit,
    revertCamera: () -> Unit,
    takePhoto: () -> Unit,
    scale: Float
) {
    val scaleStep = 0.1f // 缩放按钮的步长
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            //modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            // AI魔法按钮
            IconButton(
                onClick = {  },
                modifier = Modifier.size(35.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(0xFF6200EE).copy(alpha = 0.2f),
                    contentColor = Color(0xFF6200EE)
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.magic), // 使用魔法棒图标
                    contentDescription = "AI Magic",
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            // 翻转摄像机按钮
            IconButton(
                onClick = revertCamera,
                modifier = Modifier.size(35.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(0xFFEAEBED), // 银灰色背景
                    contentColor = Color(0xFF505661)   // 深灰色图标
                    //  containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    //  contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            ) {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = "Switch Camera",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        // 拍照按钮
        FloatingActionButton(
            onClick = { takePhoto() },
            containerColor = Purple40, // 外圈颜色
            contentColor = Color.White, // 内圈颜色
            modifier = Modifier.size(64.dp),
            shape = CircleShape
        ) {
            // 使用 Canvas 绘制双层圆形（外圈 Purple40，内圈白色）
            Canvas(modifier = Modifier.fillMaxSize()) {
                // 外圈
                drawCircle(
                    color = Purple40,
                    radius = size.minDimension / 2
                )
                // 内圈（比外圈小 20%）
                drawCircle(
                    color = Color.White,
                    radius = size.minDimension / 2 * 0.8f
                )
            }
        }

        // 比例控制
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = { updateScale(scaleStep) }) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Zoom In",
                    tint = Color(0xFF505661),
                )
            }

            Text(
                text = if (scale < 5f) String.format("%.1f", scale) + "x" else "Max",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = Color(0xFF505661),
            )

            IconButton(onClick = { updateScale(-scaleStep) }) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Zoom Out",
                    tint = Color(0xFF505661),
                )
            }
        }
//        Column(
//            modifier = Modifier.size(48.dp),
//            verticalArrangement = Arrangement.Center
//        ) {
//            IconButton(
//                onClick = {
//                    revertCamera()
//                },
//                modifier = Modifier.size(48.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Filled.Refresh,
//                    contentDescription = "翻转摄像头",
//                    modifier = Modifier.fillMaxSize()
//                )
//            }
//            Text(
//                modifier = Modifier.fillMaxWidth(),
//                text = "翻转",
//                textAlign = TextAlign.Center,
//                fontSize = 16.sp,
//            )
//        }
//        // 拍照按钮
//
//        // 缩放按钮,实现刻度尺比较麻烦，这里使用两个按钮进行最小若干距离的缩放
//        Column(
//            modifier = Modifier.width(48.dp),
//            verticalArrangement = Arrangement.Center
//        ) {
//            // 增加
//            IconButton(
//                onClick = {
//                    updateScale(scaleStep)
//                },
//                modifier = Modifier.size(48.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Filled.KeyboardArrowUp,
//                    contentDescription = "减小缩放倍数",
//                    modifier = Modifier.fillMaxSize()
//                )
//            }
//            Text(
//                modifier = Modifier.fillMaxWidth(),
//                text = if (scale < 5f) {
//                    String.format("%.2f", scale)
//                } else "Max",
//                textAlign = TextAlign.Center,
//                fontSize = 14.sp,
//            )
//            // 减少
//            IconButton(
//                onClick = {
//                    updateScale(-scaleStep)
//                },
//                modifier = Modifier.size(48.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Filled.KeyboardArrowDown,
//                    contentDescription = "增大缩放倍数",
//                    modifier = Modifier.fillMaxSize()
//                )
//            }
//            Text(
//                modifier = Modifier.fillMaxWidth(),
//                text = "缩放",
//                textAlign = TextAlign.Center,
//                fontSize = 16.sp,
//            )
//        }
    }
}