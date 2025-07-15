package cn.aicamera.frontend.ui.profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import cn.aicamera.frontend.MainActivity
import cn.aicamera.frontend.R
import cn.aicamera.frontend.common.SelectPicture
import cn.aicamera.frontend.model.BottomNavItem
import cn.aicamera.frontend.ui.component.BottomBar
import cn.aicamera.frontend.ui.theme.Purple40
import cn.aicamera.frontend.ui.theme.PurpleGrey40
import cn.aicamera.frontend.viewmodel.UserViewModel
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState", "PermissionLaunchedDuringComposition")
@Composable
fun ProfileScreen(navController: NavController, viewModel: UserViewModel) {
    val context = LocalContext.current
    val currentRoute by remember { mutableStateOf(BottomNavItem.Profile.route) }

    val selectedImageUris = remember { mutableStateListOf<Uri>() }
    val galleryPermissionState =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)
        } else { // Android 12 及以下
            rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

    val user by viewModel.user.collectAsState()
    val avatar by viewModel.avatar.collectAsState()
    val showEditDialog = remember { mutableStateOf(false) }
    val showAvatarDialog = remember { mutableStateOf(false) }
    val showConfirmDialog = remember { mutableStateOf(false) }

    val genderOptions = listOf("男", "女", "其他")
    LaunchedEffect(Unit) {
        viewModel.getProfile (
            onSuccess = {
                viewModel.loadAvatar(context) { message ->
                    Toast.makeText(context, "头像加载失败:$message", Toast.LENGTH_LONG).show()
                }
            },
            onFailed = { message ->
            Toast.makeText(context, "获取个人信息失败:$message", Toast.LENGTH_LONG).show()
        })

    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    // 设置按钮
                    IconButton(onClick = { navController.navigate("settings") }) {
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 头像
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.2f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .clickable { showAvatarDialog.value = true }
                ) {
                    AsyncImage(
                        model = avatar,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(start = 20.dp,end = 20.dp)
                    .fillMaxWidth()
                    .weight(0.7f),
                verticalArrangement = Arrangement.Center
            ) {
                // 个人信息
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "邮箱",
                        fontSize = 18.sp, // 字号变小
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), // 浅色
                        modifier = Modifier.weight(0.25f)
                    )
                    Text(
                        text = user.email,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f), // 深色
                        modifier = Modifier.weight(0.75f)
                    )
                }
                CustomDivider()
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "昵称",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.weight(0.25f)
                    )
                    Text(
                        text = user.username,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        modifier = Modifier.weight(0.75f)
                    )
                }
                CustomDivider()
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "性别",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.weight(0.25f)
                    )
                    Text(
                        text = genderOptions[user.gender],
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        modifier = Modifier.weight(0.75f)
                    )
                }
                CustomDivider()
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "年龄",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.weight(0.25f)
                    )
                    Text(
                        text = user.age.toString(),
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        modifier = Modifier.weight(0.75f)
                    )
                }
                CustomDivider()
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "爱好",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.weight(0.25f)
                    )
                    Text(
                        text = user.preference,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        modifier = Modifier.weight(0.75f)
                    )
                }
                CustomDivider()
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                // 修改信息按钮
                Button(onClick = { showEditDialog.value = true },
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors = ButtonColors(
                        containerColor = Purple40,
                        contentColor = Color.White,
                        disabledContainerColor = PurpleGrey40,
                        disabledContentColor = Color.White
                    )
                ) {
                    Text("修改个人信息")
                }
            }
        }
    }

    // 弹出修改个人信息卡片
    EditProfileDialog(
        showEditDialog.value,
        context,
        viewModel,
        onDismiss = { showEditDialog.value = false })

    // 弹出修改头像对话框
    ChooseAvatar(
        showAvatarDialog.value,
        context,
        galleryPermissionState,
        onSelectImage = {
            selectedImageUris.clear()
            selectedImageUris.add(it)
        },
        onDismiss = {
            showAvatarDialog.value = false
        },
         onShowConfirm = {
            showConfirmDialog.value = true
        }

    )
    ChangeAvatarDialog(
        showConfirmDialog.value,
        context,
        viewModel,
        selectedImageUris,
        onDismiss = {
            showConfirmDialog.value = false
            viewModel.loadAvatar(context, onFailed = { message ->
                Toast.makeText(context, "加载头像失败:$message", Toast.LENGTH_LONG).show()
            })
        })
}

@Composable
fun EditProfileDialog(
    showEditProfile: Boolean,
    context: Context,
    viewModel: UserViewModel,
    onDismiss: () -> Unit
) {
    if (showEditProfile) {
        val username = remember { mutableStateOf(viewModel.user.value.username) }
        val gender = remember { mutableStateOf(viewModel.user.value.gender) }
        val age = remember { mutableStateOf(viewModel.user.value.age.toString()) }
        val email = remember { mutableStateOf(viewModel.user.value.email) }
        val preference = remember { mutableStateOf(viewModel.user.value.preference) }

        val genderOptions = listOf("男", "女", "其他")
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("修改个人信息") },
            text = {
                Column(modifier = Modifier.padding(16.dp)) {
                    // 昵称 (最多20字)
                    OutlinedTextField(
                        value = username.value,
                        onValueChange = { if (it.length <= 20) username.value = it },
                        label = { Text("昵称(不超过20字)") }
                    )

                    // 性别选择
                    Row(modifier = Modifier.selectableGroup()) {
                        genderOptions.forEachIndexed { index, text ->
                            Row(
                                Modifier
                                    .height(56.dp)
                                    .selectable(
                                        selected = (index == gender.value),
                                        onClick = { gender.value = index },
                                        role = Role.RadioButton
                                    )
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (index == gender.value),
                                    onClick = null
                                )
                                Text(
                                    text = text,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                    }

                    // 年龄 (限制 1-99)
                    OutlinedTextField(
                        value = age.value,
                        onValueChange = {
                            val newValue = it.filter { char -> char.isDigit() }
                            if (newValue.isNotEmpty() && newValue.toIntOrNull() in 1..99) {
                                age.value = newValue
                            }
                        },
                        label = { Text("年龄(1~99)") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )

                    // 爱好 (最多50字)
                    OutlinedTextField(
                        value = preference.value,
                        onValueChange = { if (it.length <= 50) preference.value = it },
                        label = { Text("爱好(不超过50字)") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.updateProfile(username.value,
                        gender.value,
                        age.value.toIntOrNull() ?: 0,
                        email.value,
                        preference.value,
                        onSuccess = {
                            onDismiss()
                        },
                        onFailed = { message ->
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        })
                }) {
                    Text("保存")
                }
            },
            dismissButton = { Button(onClick = onDismiss) { Text("取消") } }
        )
    }
}

@SuppressLint("PermissionLaunchedDuringComposition")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ChooseAvatar(
    showAvatarDialog: Boolean,
    context: Context,
    galleryPermissionState: PermissionState,
    onSelectImage: (Uri) -> Unit,
    onDismiss: () -> Unit,
    onShowConfirm: ()->Unit
) {

    if (showAvatarDialog) {
        var showGallery by remember { mutableStateOf(false) }
        if (galleryPermissionState.status.isGranted) {
            showGallery = true
        } else {
            galleryPermissionState.launchPermissionRequest()
        }
        TryOpenGallery(
            context, showGallery,
            onSelectImage = {
                onSelectImage(it)
                showGallery = false
                onShowConfirm()
            },
            onDismiss = {
                onDismiss()
                showGallery = false
            }
        )
    }
}

@Composable
fun ChangeAvatarDialog(
    showConfirmDialog: Boolean,
    context: Context,
    viewModel: UserViewModel,
    selectedImageUris: List<Uri>,
    onDismiss: () -> Unit
) {
    if(showConfirmDialog){
        if (selectedImageUris.size != 1) {
            Toast.makeText(context, "请选择一张图片", Toast.LENGTH_LONG).show()
            return
        }
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("更换头像") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = selectedImageUris[0],
                        contentDescription = "预览图",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // 按钮行
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center // 按钮居中
                    ) {
                        // 取消按钮
                        Button(onClick = onDismiss) {
                            Text("取消更改")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        // 确定按钮
                        Button(onClick = {
                            viewModel.uploadAvatar(
                                context,
                                selectedImageUris[0],
                                onSuccess = { onDismiss() },
                                onFailed = { message ->
                                    Toast.makeText(context, "图片上传失败:$message", Toast.LENGTH_LONG)
                                        .show()
                                }
                            )
                        }) {
                            Text("确定更改")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {},
        )
    }
}

@Composable
private fun TryOpenGallery(
    context: Context,
    showGallery: Boolean,
    onSelectImage: (Uri) -> Unit,
    onDismiss: () -> Unit
) {
    var openGalleryLauncher: ManagedActivityResultLauncher<Unit?, Uri?>? =
        rememberLauncherForActivityResult(contract = SelectPicture()) { uri ->
            if (uri != null) {
                onSelectImage(uri)
                onDismiss()
            } else {
                Toast.makeText(context, "请至少选择一张图片", Toast.LENGTH_LONG).show()
                onDismiss()
            }
        }
    if (showGallery) {
        SideEffect {
            if (openGalleryLauncher != null) {
                openGalleryLauncher.launch(null)
            }
        }
    }
}

@Composable
fun CustomDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(top = 15.dp, bottom = 15.dp),
        color = Color.LightGray,
        thickness = 1.dp,
    )
}