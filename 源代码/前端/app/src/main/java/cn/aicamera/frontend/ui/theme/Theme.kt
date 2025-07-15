package cn.aicamera.frontend.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 定义浅色主题颜色
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE), // 主色调
    secondary = Color(0xFF03DAC6), // 次要色调
    background = Color(0xFFFFFFFF), // 背景色
    surface = Color(0xFFFFFFFF), // 表面色
    onPrimary = Color(0xFFFFFFFF), // 主色调上的文字颜色
    onSecondary = Color(0xFF000000), // 次要色调上的文字颜色
    onBackground = Color(0xFF000000), // 背景上的文字颜色
    onSurface = Color(0xFF000000), // 表面上的文字颜色
)
// 定义深色主题颜色
private val DarkColorScheme = darkColorScheme(
        primary = Color(0xFFBB86FC), // 主色调
secondary = Color(0xFF03DAC6), // 次要色调
background = Color(0xFF121212), // 背景色
surface = Color(0xFF1E1E1E), // 表面色
onPrimary = Color(0xFF000000), // 主色调上的文字颜色
onSecondary = Color(0xFF000000), // 次要色调上的文字颜色
onBackground = Color(0xFFFFFFFF), // 背景上的文字颜色
onSurface = Color(0xFFFFFFFF), // 表面上的文字颜色
)
// 定义字体样式
private val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// 定义形状（圆角）
private val AppShapes = androidx.compose.material3.Shapes(
    small = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
)

// 自定义主题
@Composable
fun CameraAppTheme(
    darkTheme: Boolean = false, // 默认使用浅色主题
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}