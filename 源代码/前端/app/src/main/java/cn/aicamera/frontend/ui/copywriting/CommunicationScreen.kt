package cn.aicamera.frontend.ui.copywriting

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.aicamera.frontend.MainActivity
import cn.aicamera.frontend.R
import cn.aicamera.frontend.model.Message
import cn.aicamera.frontend.utils.FileUtils
import cn.aicamera.frontend.viewmodel.ChatViewModel
import cn.aicamera.frontend.viewmodel.UserViewModel
import coil.compose.AsyncImage
import kotlin.math.floor

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunicationScreen(
    chatViewModel: ChatViewModel,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val initialized = remember { mutableStateOf(false) }
    if (!initialized.value) {
        chatViewModel.initMessageBox()
        chatViewModel.addMessage(
            Message(
                text = stringResource(R.string.chat_welcome),
                isUser = false
            )
        )
        initialized.value = true
    }
    val loaded = remember { mutableStateOf(false) }
    val originImageUris by chatViewModel.imageUris.collectAsState() // 传入的图片 URI 列表
    val imageUris = remember { mutableStateOf(emptyList<Uri>()) }
    val messages by chatViewModel.messages.collectAsState()
    val scrollState = rememberLazyListState()

    val defaultText = stringResource(R.string.chat_default)
    var inputText by remember { mutableStateOf(defaultText) }

    // 上传照片
    var count = remember { mutableStateOf(0) }
    if (!loaded.value) {
        originImageUris.forEachIndexed { index, imageUri ->
            val file = FileUtils.uriToFile(uri = imageUri, context = context)
            if (file == null) Toast.makeText(
                context,
                "第${index + 1}张照片打开失败",
                Toast.LENGTH_LONG
            ).show()
            else {
                chatViewModel.uploadImageToServer(
                    file, {
                        count.value++
                        imageUris.value += imageUri
                    },
                    { message ->
                        Toast.makeText(
                            context,
                            "第${index + 1}张照片上传失败,${message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }
        }
        loaded.value = true
    }
    userViewModel.getProfile({},{})
    userViewModel.loadAvatar(context, onFailed = {message->
        Toast.makeText(context,"获取用户头像失败:$message",Toast.LENGTH_LONG).show()
    })
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.height(48.dp),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                title = {
                    Text(
                        text = stringResource(R.string.chat_page_name),
                        color = Color.Black.copy(0.9f),
                        modifier = Modifier.padding(top = 7.dp) // 微调
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)) {
            ImageGallery(imageUris.value, count.value == imageUris.value.size) // 顶部图片展示
            HorizontalDivider(thickness = 1.dp)
            Spacer(modifier = Modifier.height(5.dp))
            HorizontalDivider(thickness = 1.dp)
            LazyColumn(
                state = scrollState,
                reverseLayout = true, // 最新消息在底部
                modifier = Modifier.weight(1f)
            ) {
                items(messages.reversed()) { message ->
                    ChatBubble(message,userViewModel.avatar.value)
                }
            }

            ChatInputField(
                inputText = inputText,
                onTextChange = { inputText = it },
                onSendMessage = {
                    if (inputText.isNotBlank()) {
                        chatViewModel.sendMessage(inputText.trim())
                        inputText = ""
                    }
                }
            )
        }
    }

}

/**
 * 聊天气泡
 */

/**
 * 聊天气泡
 */
@Composable
fun ChatBubble(message: Message,userAvatar:Bitmap?) {
    val arrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    val backgroundColor =
        if (message.isUser) MaterialTheme.colorScheme.surfaceVariant else Color.LightGray

    val screenWidth = LocalConfiguration.current.screenWidthDp
    val boxWidth = floor(screenWidth * 0.7f.toDouble())
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = arrangement
    ) {
        // AI头像
        if (!message.isUser) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(top = 8.dp, start = 8.dp)
                    .background(Color.Black, shape = RoundedCornerShape(48.dp))
            )
        }
        Box(
            modifier = Modifier
                .widthIn(max = boxWidth.dp)
                .heightIn(min = 56.dp)
                .padding(8.dp)
                .background(backgroundColor, shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = message.text,
                color = Color.Black,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(start = 8.dp, end = 4.dp, top = 4.dp, bottom = 5.dp)
            )
        }
        // 用户头像
        if (message.isUser) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(top = 8.dp, end = 8.dp)
                    .background(Color.Black, shape = RoundedCornerShape(48.dp))
            ) {
                AsyncImage(
                    model = userAvatar,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

/**
 * 展示图片
 */

/**
 * 展示图片
 */
@Composable
fun ImageGallery(imageUris: List<Uri>, finishUpload: Boolean) {
    if (imageUris.isNotEmpty() && finishUpload) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)

        ) {
            Text(
                text = "图片预览",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(4.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(imageUris.take(9)) { uri -> // 最多显示 9 张图片
                    ImageThumbnail(uri)
                }
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when {
                    finishUpload -> "图片上传失败"
                    else -> "图片上传中..."
                },
                fontSize = 24.sp
            )
        }
    }
}

@Composable
fun ImageThumbnail(uri: Uri) {
    AsyncImage(
        model = uri,
        contentDescription = "预览图",
        modifier = Modifier
            .size(80.dp) // 缩略图大小
            .clip(RoundedCornerShape(8.dp))
            .border(2.dp, Color.Gray, RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
}

/**
 * 输入框部分
 */

/**
 * 输入框部分
 */
@Composable
fun ChatInputField(
    inputText: String,
    onTextChange: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = inputText,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("输入消息...") },
        )
        IconButton(
            modifier = Modifier
                .size(48.dp)
                .padding(12.dp),
            onClick = onSendMessage
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ChatBubble() {
    val arrangement = if (false) Arrangement.End else Arrangement.End
    val backgroundColor = if (false) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFEAE1D9)

    val screenWidth = LocalConfiguration.current.screenWidthDp
    val boxWidth = floor(screenWidth * 0.7f.toDouble())
    Row(
        modifier = Modifier
            .padding(top = 100.dp)
            .fillMaxWidth(),
        horizontalArrangement = arrangement
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .padding(top = 8.dp, start = 8.dp)
                .background(Color.Black, shape = RoundedCornerShape(48.dp))
        )
        Box(
            modifier = Modifier
                .width(boxWidth.dp)
                .heightIn(min = 56.dp)
                .padding(8.dp)
                .background(backgroundColor, shape = RoundedCornerShape(8.dp))
        ) {
            Text(
                text = stringResource(R.string.chat_welcome),
                color = Color.Black.copy(0.8f),
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(start = 4.dp, end = 2.dp, top = 2.dp, bottom = 2.dp)
            )
        }
    }
}


