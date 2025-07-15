package cn.aicamera.frontend.ui.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import cn.aicamera.frontend.R
import cn.aicamera.frontend.viewmodel.UserViewModel

@Composable
fun AuthScreen(
    userViewModel: UserViewModel = hiltViewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    var isRegistering by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App图标
        Image(
            painter = painterResource(id = R.drawable.ic_app), // 替换为你的应用图标资源
            contentDescription = "App Icon",
            modifier = Modifier.size(150.dp)
        )

        // 标题
        Text(
            text = stringResource(R.string.app_name),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        if (isRegistering) {
            RegisterScreen(context, userViewModel) { isRegistering = false }
        } else {
            LoginScreen(context, navController, userViewModel) { isRegistering = true }
        }
    }
}

@Composable
fun LoginScreen(
    context: Context,
    navController: NavController,
    userViewModel: UserViewModel,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = { Text("邮箱") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                if (it.length <= 16) {
                    password = it
                }
            },
            label = { Text("密码(不多于16位)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        )

        Button(
            onClick = {
                if(!email.matches(Regex(".+@.+\\..+"))){
                    Toast.makeText(context,"请输入正确格式的邮箱账号",Toast.LENGTH_LONG).show()
                }
                else if(password==null||password.length<=0||password.length>20){
                    Toast.makeText(context,"密码长度应在1~16之间",Toast.LENGTH_LONG).show()
                }
                else{
                    userViewModel.login(email = email, password = password,
                        onSuccess = {
                            Toast.makeText(context, "登录成功！", Toast.LENGTH_SHORT).show()
                            navController.navigate("home") {
                                popUpTo("auth") { // 弹出换个页面
                                    inclusive = true
                                }
                            }
                        }, onFailed = { message ->
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        })
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("登录")
        }

        TextButton(onClick = onRegisterClick) {
            Text("没有账号？注册")
        }
    }
}

@Composable
fun RegisterScreen(context: Context, userViewModel: UserViewModel, onLoginClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("邮箱") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("昵称") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Button(
            onClick = {
                if(!email.matches(Regex(".+@.+\\..+"))){
                    Toast.makeText(context,"请输入正确格式的邮箱账号",Toast.LENGTH_LONG).show()
                }
                else if(password==null||password.length<=0||password.length>16){
                    Toast.makeText(context,"密码长度应在1~16之间",Toast.LENGTH_LONG).show()
                }
                else if(username==null||username.length<=0||username.length>20){
                    Toast.makeText(context,"昵称长度应在1~20之间",Toast.LENGTH_LONG).show()
                }
                else{
                    userViewModel.register(email = email, username = username, password = password,
                        onSuccess = {
                            Toast.makeText(context, "注册成功，请牢记邮箱名和密码", Toast.LENGTH_SHORT).show()
                            onLoginClick()
                        },
                        onFailed = { message ->
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        })
                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("注册")
        }

        TextButton(onClick = onLoginClick) {
            Text("已有账号？登录")
        }
    }
}
