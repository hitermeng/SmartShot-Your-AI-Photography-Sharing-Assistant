package cn.aicamera.frontend.ui


import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import cn.aicamera.frontend.R
import cn.aicamera.frontend.common.RouteConfig
import cn.aicamera.frontend.model.BottomNavItem
import cn.aicamera.frontend.ui.camera.CameraActivity
import cn.aicamera.frontend.ui.component.BottomBar
import cn.aicamera.frontend.ui.copywriting.CopywritingActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current
    var currentRoute by remember { mutableStateOf(BottomNavItem.Home.route) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
//                colors = TopAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
//                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
//                    titleContentColor = MaterialTheme.colorScheme.primary,
//                    actionIconContentColor = Color.Black
//                ),
                actions = {
                    // 设置按钮
                    IconButton(onClick = {
                        navController.navigate("settings") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_settings),
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomBar(currentRoute, navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 照相页面
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth()
//                    .background(Color.LightGray, RoundedCornerShape(15.dp))
            ) {
                Text(
                    text = "智能拍照引导",
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                )
                ImageButton(
                    imageId = R.drawable.ic_camera_page, description = "智能拍照引导",
                    onClick = {
                        val intent = Intent(context, CameraActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 文案生成页面
            Column(
                modifier = Modifier.weight(0.5f)
            ) {
                ImageButton(
                    imageId = R.drawable.ic_chat_page, description = "图片自动生成文案",
                    onClick = {
                        val intent = Intent(context, CopywritingActivity::class.java)
                        context.startActivity(intent)
                    }
                )
                Text(
                    text = "图片自动生成文案",
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

//@Composable
//fun RoundedButton(text: String, onClick: () -> Unit) {
//    Button(
//        onClick = onClick,
//        shape = RoundedCornerShape(50),
//        colors = ButtonDefaults.buttonColors(
//            containerColor = MaterialTheme.colorScheme.primary,
//            contentColor = Color.White
//        ),
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(60.dp)
//    ) {
//        Text(
//            text = text,
//            fontSize = 18.sp,
//            fontWeight = FontWeight.Bold
//        )
//    }
//}
@Composable
fun ImageButton(onClick: () -> Unit, imageId: Int, description: String) {
    Image(
        painter = painterResource(id = imageId),
        contentDescription = description,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp)) // 圆角裁剪
            .clickable(onClick = onClick) // 点击事
    )
}

@Preview
@Composable
fun Test() {
    MainScreen(rememberNavController())
}
