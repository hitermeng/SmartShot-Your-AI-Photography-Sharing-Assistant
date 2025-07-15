package cn.aicamera.frontend.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.aicamera.frontend.common.RouteConfig
import cn.aicamera.frontend.ui.auth.AuthScreen
import cn.aicamera.frontend.ui.copywriting.CommunicationScreen
import cn.aicamera.frontend.ui.copywriting.ImageChooseScreen
import cn.aicamera.frontend.ui.profile.ProfileScreen
import cn.aicamera.frontend.ui.setting.SettingScreen
import cn.aicamera.frontend.viewmodel.ChatViewModel
import cn.aicamera.frontend.viewmodel.UserViewModel

@Composable
fun AppNavHost(startPage: RouteConfig) {

    val navController = rememberNavController()
    val chatViewModel: ChatViewModel = hiltViewModel() // 为了在两个组件间共享数据，需要提取到这里
    val userViewModel: UserViewModel = hiltViewModel()
    NavHost(navController, startDestination = startPage.toString()) {
        composable(RouteConfig.AUTH.toString()) {
            AuthScreen(navController = navController)
        }
        // 主界面
        composable(RouteConfig.HOME.toString()) {
            MainScreen(navController = navController)
        }
        // 个人中心页面
        composable(RouteConfig.PROFILE.toString()) {
            ProfileScreen(navController,userViewModel)
        }
//        // 相机页面
//        composable("camera") {
//            CameraScreen()
//        }
//        // 文案页面
        composable(RouteConfig.CHAT.toString()) {
            CommunicationScreen(chatViewModel,userViewModel)
        }
        composable(RouteConfig.IMAGE_CHOOSE.toString()){
            ImageChooseScreen(navController,chatViewModel)
        }
        // 设置界面
        composable(RouteConfig.SETTINGS.toString()) {
            SettingScreen(navController, userViewModel)
        }
//
    }
}