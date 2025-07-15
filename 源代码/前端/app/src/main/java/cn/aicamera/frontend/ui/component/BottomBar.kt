package cn.aicamera.frontend.ui.component

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import cn.aicamera.frontend.common.RouteConfig
import cn.aicamera.frontend.model.BottomNavItem
import cn.aicamera.frontend.ui.theme.PurpleGrey80

@Composable
fun BottomBar(currentRoute: String, navController: NavController) {
    BottomAppBar(
        containerColor = PurpleGrey80,
    ) {
        val items = listOf(
            BottomNavItem.Home,
            BottomNavItem.Profile
        )
        NavigationBar {
            items.forEach { item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            item.icon,
                            contentDescription = item.label,
                        )
                    },
                    label = { Text(item.label) },
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(RouteConfig.HOME.toString()) { inclusive = false } // 不存在页面时，跳转到home
                            launchSingleTop = true
                        }
                    },
                )
            }
        }
    }
}
