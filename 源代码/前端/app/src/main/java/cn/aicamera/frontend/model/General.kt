package cn.aicamera.frontend.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "主页", Icons.Default.Home)
    object Profile : BottomNavItem("profile", "个人中心", Icons.Default.Person)
}

// 通用响应
data class SuccessResponse(
    val success: Boolean,
    val message: String,
)
data class GeneralResponse<T>(
    val success: Boolean,
    val message: String,
    val response: T? // 只有成功时返回
)