package cn.aicamera.frontend.ui.camera.screen

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import cn.aicamera.frontend.model.ImageResponse
import kotlinx.coroutines.delay
import java.util.Timer
import java.util.TimerTask
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// 箭头起始和结束的屏幕比例
enum class ArrowOffsets(val offset: Offset) {
//    LEFT_START(Offset(1.8f, 0.1f)), LEFT_END(Offset(0.8f, 0.1f)),
//    RIGHT_START(Offset(1.2f, 0.1f)), RIGHT_END(Offset(2.2f, 0.1f)),
//    UP_START(Offset(1.5f, 0.2f)), UP_END(Offset(1.5f, -0.1f)),
//    DOWN_START(Offset(1.5f, 0.1f)), DOWN_END(Offset(1.5f, 0.4f)),
//
//    LEFT_UP_START(Offset(1.8f, 0.2f)), LEFT_UP_END(Offset(0.8f, 0.01f)),
//    LEFT_DOWN_START(Offset(1.8f, 0.05f)), LEFT_DOWN_END(Offset(0.8f, 0.25f)),
//
//    RIGHT_DOWN_START(Offset(1.2f, 0.05f)), RIGHT_DOWN_END(Offset(2.2f, 0.25f)), // experiment
//    RIGHT_UP_START(Offset(1.2f, 0.2f)), RIGHT_UP_END(Offset(2.2f, 0.01f)),
//
//    FORWARD_START(Offset(0.1f, 0.4f)), FORWARD_END(Offset(0.1f, 0f)),
//    BACKWARD_START(Offset(0.1f, 0.1f)), BACKWARD_END(Offset(0.1f, 0.5f)),

    LEFT_START(Offset(2.3f, 1.0f)), LEFT_END(Offset(0.7f, 1.0f)),
    RIGHT_START(Offset(0.7f, 1.0f)), RIGHT_END(Offset(2.3f, 1.0f)),
    UP_START(Offset(1.5f, 0.2f)), UP_END(Offset(1.5f, -0.1f)),
    DOWN_START(Offset(1.5f, 0.1f)), DOWN_END(Offset(1.5f, 0.4f)),

    LEFT_UP_START(Offset(2.3f, 1.2f)), LEFT_UP_END(Offset(0.7f, 0.8f)),
    LEFT_DOWN_START(Offset(2.3f, 0.8f)), LEFT_DOWN_END(Offset(0.7f, 1.2f)),

    RIGHT_DOWN_START(Offset(0.7f, 0.8f)), RIGHT_DOWN_END(Offset(2.3f, 1.2f)), // experiment
    RIGHT_UP_START(Offset(0.7f, 1.2f)), RIGHT_UP_END(Offset(2.3f, 0.8f)),

    FORWARD_START(Offset(1.5f, 0.4f)), FORWARD_END(Offset(1.5f, 0f)),
    BACKWARD_START(Offset(1.5f, 0.1f)), BACKWARD_END(Offset(1.5f, 0.5f)),
}

/**
 * 指导用户的箭头
 */
@Composable
fun ArrowAnimation(
    isVisible:Boolean,
    imageResponse: ImageResponse,
    duration: Int,
    modifier: Modifier = Modifier,
    DisableVisible:()->Unit
) {
    val screenSize = DpSize(
        LocalConfiguration.current.screenWidthDp.dp,
        LocalConfiguration.current.screenHeightDp.dp
    )
    val infiniteTransition = rememberInfiniteTransition()

    val textMeasurer = rememberTextMeasurer()

    val start_top = remember { mutableStateOf(Offset.Zero) }
    val end_top = remember { mutableStateOf(Offset.Zero) }
    val start_fb = remember { mutableStateOf(Offset.Zero) }
    val end_fb = remember { mutableStateOf(Offset.Zero) }

    calculateStartEnd(imageResponse, screenSize) { start1, end1, start2, end2 ->
        start_top.value = start1
        end_top.value = end1
        start_fb.value = start2
        end_fb.value = end2
    }
    var timer = Timer()
    val task = object : TimerTask() {
        override fun run() {
            DisableVisible()
        }
    }
    // 定时关闭提示框
    LaunchedEffect(isVisible) {
        if (isVisible) {
            timer.cancel() // 重新定时
            timer = Timer()
            timer.schedule(
                task,6000
            )
        }
    }
    // 指引上下左右的box
    if (isVisible && start_top.value != Offset.Zero) {
        key(imageResponse){
            // 整体动画进度（0f到1f循环）
            val progress by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(duration, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ), label = ""
            )
            val wingMax =
                floor(
                    sqrt(
                        (start_top.value.x - end_top.value.x).toDouble().pow(2.0)
                                + Math.pow((start_top.value.y - end_top.value.y).toDouble(), 2.0)
                    ) / 40
                )
            // 当前显示侧翼数量，使用乘法实现区间计算
            val wingCount = ceil(progress * wingMax).toInt() - 1
            Box(modifier = modifier) {
                // 方向计算
                val angle = remember(start_top, end_top) {
                    val dx = end_top.value.x - start_top.value.x
                    val dy = end_top.value.y - start_top.value.y
                    atan2(dy, dx) * (180f / PI.toFloat())
                }
                Canvas(modifier = Modifier.fillMaxSize()) {

                    var position = Offset(0f, 0f)
                    // 绘制动态侧翼
//                repeat(wingCount) { index ->
//                    position = calculateOffsetOnLine(
//                        start_top.value,
//                        end_top.value,
//                        1 / wingMax.toFloat() * index
//                    )
//                    drawPath(
//                        path = createWingPath(position, angle),
//                        color = Color.Yellow.copy(alpha = 0.9f - index * 0.05f),
//                        style = Stroke(width = (3 - index * 0.3f).dp.toPx())
//                    )
//                }
                    repeat(wingCount) { index ->
                        position = calculateOffsetOnLine(
                            start_top.value,
                            end_top.value,
                            1 / wingMax.toFloat() * index
                        )
                        drawPath(
                            path = createWingPath(position, angle),
//                        color = Color.Yellow.copy(alpha = 0.9f - index * 0.02f),
                            color = Color.White.copy(alpha = 1.0f - index * 0.025f),
                            style = Stroke(width = (3 - index * 0.1f).dp.toPx())
                        )
                    }

                    // 文字提示
                    val measuredText = textMeasurer.measure(
                        AnnotatedString(when{
                            imageResponse.moveLeftRight > 0 -> "向右移动${abs(imageResponse.moveLeftRight)}厘米\n"
                            imageResponse.moveLeftRight < 0 -> "向左移动${abs(imageResponse.moveLeftRight)}厘米\n"
                            else -> ""
                        }+when{
                            imageResponse.moveUpDown > 0 -> "向上移动${abs(imageResponse.moveUpDown)}厘米"
                            imageResponse.moveUpDown < 0 -> "向下移动${abs(imageResponse.moveUpDown)}厘米"
                            else -> ""
                        }),
                        constraints = Constraints.fixed(
                            width = (size.width / 2f).toInt(),
                            height = (size.height / 2f).toInt()
                        ),
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            ),
                            textAlign = TextAlign.Center,
                        )
                    )
                    // 绘制文字
                    drawText(
                        textLayoutResult = measuredText,
                        color = Color.White,
                        alpha = 0.5f,
                        // 文字位置不变
                        topLeft = Offset(
                            0.25f*screenSize.width.toPx(),
                            0.01f
//                        start_top.value.x,
//                        start_top.value.y
                        ),
                    )
                }
            }
        }
    }
    // 指引前后的box
    if (isVisible && start_fb.value != Offset.Zero) {
        key(imageResponse) {
            // 整体动画进度（0f到1f循环）
            val progress by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(duration, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ), label = ""
            )
            // 最大侧翼数量
            val wingMax =
                floor(
                    sqrt(
                        (start_fb.value.x - end_fb.value.x).toDouble().pow(2.0)
                                + Math.pow((start_fb.value.y - end_fb.value.y).toDouble(), 2.0)
                    ) / 40
                )
            // 当前显示侧翼数量，使用乘法实现区间计算
            val wingCount = ceil(progress * wingMax).toInt() - 1
            Box(modifier = modifier) {
                // 方向计算
                val angle = remember(start_top, end_top) {
                    val dx = end_fb.value.x - start_fb.value.x
                    val dy = end_fb.value.y - start_fb.value.y
                    atan2(dy, dx) * (180f / PI.toFloat())
                }
                Canvas(modifier = Modifier.fillMaxSize()) {

                    var position = Offset(0f, 0f)
                    // 绘制动态侧翼
                    repeat(wingCount) { index ->
                        position = calculateOffsetOnLine(
                            start_fb.value,
                            end_fb.value,
                            1 / wingMax.toFloat() * index
                        )
                        drawPath(
                            path = createWingPath(position, angle),
//                        color = Color.Yellow.copy(alpha = 0.9f - index * 0.02f),
                            color = Color.White.copy(alpha = 1.0f - index * 0.025f),
                            style = Stroke(width = (3 - index * 0.1f).dp.toPx())
                        )
                    }

                    // 文字提示
                    val measuredText = textMeasurer.measure(
                        AnnotatedString( when {
                            start_fb.value.y > end_fb.value.y -> "向\n前\n移\n动\n${abs(imageResponse.moveForwardBackward)}\n厘\n米"// 箭头向上
                            else -> "向\n后\n移\n动\n${abs(imageResponse.moveForwardBackward)}\n厘\n米"
                        }),
                        constraints = Constraints.fixed(
                            width = (size.width / 3f).toInt(),
                            height = (size.height / 3f).toInt()
                        ),
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            )
                        )
                    )
                    // 绘制文字
                    drawText(
                        textLayoutResult = measuredText,
                        color = Color.White,
                        alpha = 0.5f,
                        // 文字位置不变
                        topLeft = when {
                            start_fb.value.y > end_fb.value.y -> Offset( // 箭头向上
                                30.dp.toPx(),
                                0f
                            )
                            else -> Offset( // 箭头向下
                                30.dp.toPx(),
                                0f
                            )
                        }
                    )
                }
            }
        }
    }
}

/**
 * 绘制一个顶点位于position，线段角度为angle的左右侧翼
 */
private fun createWingPath(position: Offset, angle: Float): Path {
    return Path().apply {
//        val wingLength = 60f  // 侧翼长度
//        val wingAngle = 25f
        val wingLength = 150f  // 侧翼长度
        val wingAngle = 55f

        // 左侧翼
        val leftOffset = calculateRotatedEndpoint(position, angle, wingLength, wingAngle)
        moveTo(position.x, position.y)
        lineTo(leftOffset.x, leftOffset.y)

        // 右侧翼
        val rightOffset = calculateRotatedEndpoint(position, angle, wingLength, -wingAngle)
        moveTo(position.x, position.y)
        lineTo(rightOffset.x, rightOffset.y)
    }
}

private fun calculateRotatedEndpoint(
    position: Offset,
    angle: Float, // 初始角度，x轴正方向顺时针，角度值
    length: Float,
    originAngle: Float // 顺时针旋转角度
): Offset {
    val newAngle = angle + 180
    val originAngleInRadians = originAngle.toFloat()
    val totalAngle = Math.toRadians((newAngle + originAngleInRadians).toDouble()).toFloat()

    val dx = length * cos(totalAngle)
    val dy = length * sin(totalAngle)

    val endX = position.x + dx
    val endY = position.y + dy

    return Offset(endX, endY)
}

// 角度转换扩展
val Float.degrees: Double get() = this * PI / 180

/**
 * 计算从start到end的线段上指定比例位置的 Offset
 */
fun calculateOffsetOnLine(start: Offset, end: Offset, ratio: Float): Offset {
    val clampedRatio = ratio.coerceIn(0f, 1f)
    val x = start.x + (end.x - start.x) * clampedRatio
    val y = start.y + (end.y - start.y) * clampedRatio
    return Offset(x, y)
}

/**
 * 根据图片分析结果计算箭头方向
 */
fun calculateStartEnd(
    imageResponse: ImageResponse,
    screenSize: DpSize, // 屏幕尺寸
    changeStartEnd: (Offset, Offset, Offset, Offset) -> Unit
) {

    val (grade,moveUpDown, moveLeftRight, moveForwardBackward) = imageResponse
//    var horizontalStart = Offset.Zero
//    var horizontalEnd = Offset.Zero
    // 获取屏幕宽度和高度
    val screenWidth = screenSize.width.value
    val screenHeight = screenSize.height.value

    // 根据 moveUpDown 和 moveLeftRight 确定上下左右箭头的方向
    val (horizontalStart, horizontalEnd) = when {
        moveUpDown > 0 && moveLeftRight > 0 -> {
            // 右上
            ArrowOffsets.RIGHT_UP_START.offset to ArrowOffsets.RIGHT_UP_END.offset
        }

        moveUpDown > 0 && moveLeftRight < 0 -> {
            // 左上
            ArrowOffsets.LEFT_UP_START.offset to ArrowOffsets.LEFT_UP_END.offset
        }

        moveUpDown < 0 && moveLeftRight > 0 -> {
            // 右下
            ArrowOffsets.RIGHT_DOWN_START.offset to ArrowOffsets.RIGHT_DOWN_END.offset
        }

        moveUpDown < 0 && moveLeftRight < 0 -> {
            // 左下
            ArrowOffsets.LEFT_DOWN_START.offset to ArrowOffsets.LEFT_DOWN_END.offset
        }

        moveUpDown > 0 -> {
            // 上
            ArrowOffsets.UP_START.offset to ArrowOffsets.UP_END.offset
        }

        moveUpDown < 0 -> {
            // 下
            ArrowOffsets.DOWN_START.offset to ArrowOffsets.DOWN_END.offset
        }

        moveLeftRight > 0 -> {
            // 右
            ArrowOffsets.RIGHT_START.offset to ArrowOffsets.RIGHT_END.offset
        }

        moveLeftRight < 0 -> {
            // 左
            ArrowOffsets.LEFT_START.offset to ArrowOffsets.LEFT_END.offset
        }

        else -> {
            // 默认无箭头
            Offset.Zero to Offset.Zero
        }
    }

    // 根据 moveForwardBackward 确定前后箭头的方向
    val (forwardStart, forwardEnd) = when {
        moveForwardBackward > 0 -> {
            // 向前
            ArrowOffsets.FORWARD_START.offset to ArrowOffsets.FORWARD_END.offset
        }

        moveForwardBackward < 0 -> {
            // 向后
            ArrowOffsets.BACKWARD_START.offset to ArrowOffsets.BACKWARD_END.offset
        }

        else -> {
            // 默认无箭头
            Offset.Zero to Offset.Zero
        }
    }

    // 将比例坐标转换为屏幕坐标
    fun convertOffset(arrowOffset: Offset): Offset {
        return Offset(
            arrowOffset.x * screenWidth,
            arrowOffset.y * screenHeight
        )
    }

    // 调用回调函数，返回箭头的起止点
    changeStartEnd(
        convertOffset(horizontalStart),
        convertOffset(horizontalEnd),
        convertOffset(forwardStart),
        convertOffset(forwardEnd)
    )
}

// 使用示例
@Preview
@Composable
fun AnimationPreview() {

}
