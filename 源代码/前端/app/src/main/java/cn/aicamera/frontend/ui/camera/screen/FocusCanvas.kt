package cn.aicamera.frontend.ui.camera.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp


@Composable
fun FocusCanvas(focusPoint: Offset?, focusIndicatorAlpha: Float) {
            // 修改后的聚焦框实现
            focusPoint?.let { point ->
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
//                .pointerInput(Unit) {
//                    detectTapGestures { tapOffset ->
//
//                    }
//                }
                ) {
                    val strokeWidth = 2.dp.toPx()
                    val boxSize = 80.dp.toPx()
                    val cornerSize = 16.dp.toPx()

                    // 绘制聚焦框
                    drawPath(
                        path = Path().apply {
                            // 左上角
                            moveTo(point.x - boxSize / 2, point.y - boxSize / 2 + cornerSize)
                            lineTo(point.x - boxSize / 2, point.y - boxSize / 2)
                            lineTo(point.x - boxSize / 2 + cornerSize, point.y - boxSize / 2)

                            // 右上角
                            moveTo(point.x + boxSize / 2 - cornerSize, point.y - boxSize / 2)
                            lineTo(point.x + boxSize / 2, point.y - boxSize / 2)
                            lineTo(point.x + boxSize / 2, point.y - boxSize / 2 + cornerSize)

                            // 右下角
                            moveTo(point.x + boxSize / 2, point.y + boxSize / 2 - cornerSize)
                            lineTo(point.x + boxSize / 2, point.y + boxSize / 2)
                            lineTo(point.x + boxSize / 2 - cornerSize, point.y + boxSize / 2)

                            // 左下角
                            moveTo(point.x - boxSize / 2 + cornerSize, point.y + boxSize / 2)
                            lineTo(point.x - boxSize / 2, point.y + boxSize / 2)
                            lineTo(point.x - boxSize / 2, point.y + boxSize / 2 - cornerSize)
                        },
                        color = Color.Yellow.copy(alpha = focusIndicatorAlpha),
                        style = Stroke(strokeWidth)
                    )

                    // 绘制中心点
                    drawCircle(
                        color = Color.Yellow.copy(alpha = focusIndicatorAlpha),
                        radius = 4.dp.toPx(),
                        center = point
                    )
                }
            }
        }
