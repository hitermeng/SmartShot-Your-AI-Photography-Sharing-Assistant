package cn.aicamera.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import cn.aicamera.frontend.common.RouteConfig
import cn.aicamera.frontend.ui.AppNavHost
import cn.aicamera.frontend.ui.MainScreen
import cn.aicamera.frontend.ui.auth.AuthScreen
import cn.aicamera.frontend.ui.theme.CameraAppTheme
import cn.aicamera.frontend.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel.validateLogin(
            onSuccess = {
                setContent {
                    CameraAppTheme { // 写在里面避免执行顺序的问题
                        AppNavHost(RouteConfig.HOME)
                    }
                }
            },
            onFailed = {
                setContent {
                    AppNavHost(RouteConfig.AUTH)
                }
            }
        )

    }

}

@Preview
@Composable
fun TestView() {
    CameraAppTheme {
        val navController = rememberNavController()
        MainScreen(navController = navController)
    }
}
