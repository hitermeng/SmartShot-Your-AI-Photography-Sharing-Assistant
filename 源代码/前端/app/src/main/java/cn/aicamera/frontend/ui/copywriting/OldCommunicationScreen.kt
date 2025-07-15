//package cn.aicamera.frontend.ui.copywriting
//
//import android.content.ContentResolver
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.net.Uri
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.isSystemInDarkTheme
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.IntrinsicSize
//import androidx.compose.foundation.layout.RowScope
//import androidx.compose.foundation.layout.aspectRatio
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Attachment
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.LocalContentColor
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.drawBehind
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.asImageBitmap
//import androidx.compose.ui.platform.LocalConfiguration
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.testTag
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import io.getstream.chat.android.client.ChatClient
//import io.getstream.chat.android.compose.ui.messages.MessagesScreen
//import io.getstream.chat.android.compose.ui.theme.ChatComponentFactory
//import io.getstream.chat.android.compose.ui.theme.ChatTheme
//import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory
//import io.getstream.chat.android.models.Attachment
//import io.getstream.chat.android.models.Channel
//import io.getstream.chat.android.models.ConnectionState
//import io.getstream.chat.android.models.Message
//import io.getstream.chat.android.models.User
//import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
//import io.getstream.chat.android.state.plugin.config.StatePluginConfig
//import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory
//import io.getstream.chat.android.ui.common.state.messages.MessageMode
//import io.getstream.chat.android.ui.common.state.messages.composer.MessageComposerState
//import java.io.File
//import java.io.FileOutputStream
//import java.io.InputStream
//
//@Composable
//fun OldCommunicationScreen(imagePath: Uri?){
//    val context = LocalContext.current
//
//    val channelId = "messaging:general"
//    val viewModelFactory = MessagesViewModelFactory(context = context,channelId = channelId)
//
//    if(imagePath != null){
//        // 自动发送图片
//        LaunchedEffect(Unit) {
//            sendImage(context,imagePath)
//        }
//    }
//    Column {
//
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(0.9f)
//        ){
//
//
//            ChatTheme(
//                componentFactory = CustomChatComponentFactory()
//            ) {
//                MessagesScreen(
//                    viewModelFactory = viewModelFactory
//                )
//            }
//        }
//    }
//}
//
///**
// * 原本的图片展示盒
// */
//@Composable
//fun PhotoBox(context : Context,imagePath :Uri?){
//    val configuration = LocalConfiguration.current
//
//    var bitmap : Bitmap? = null
//    try {
//        if(imagePath == null) throw NullPointerException("Image path is null")
//        val inputStream = context.contentResolver.openInputStream(imagePath)
//        bitmap = BitmapFactory.decodeStream(inputStream)
//
//    } catch (e: Exception) {
//        e.printStackTrace()
//        Text("图片加载失败或被删除")
//    }
//
//    var boxHeight by remember { mutableStateOf(Math.floor(configuration.screenHeightDp*0.2).toInt()) }
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(boxHeight.dp)
//            .drawBehind { // 绘制一条边框底部的线
//                val strokeWidth = 1f * density // 线的粗细，1为1dp
//                val y = size.height // 高度，size.height为box的高度
//                drawLine(
//                    Color.LightGray,
//                    Offset(0f, y),
//                    Offset(size.width, y),
//                    strokeWidth
//                )
//            },
//        contentAlignment = Alignment.Center,
//    ){
//        if(bitmap != null){
//            val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
//            val targetWidth = (boxHeight * aspectRatio).toInt()
//            Image(
//                bitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, boxHeight, false).asImageBitmap(),
//                contentDescription = "待处理图片",
//                modifier = Modifier
//                    .fillMaxHeight()
//                    .aspectRatio(aspectRatio),
//            )
//        }
//    }
//}
//
//class CustomChatComponentFactory : ChatComponentFactory {
//    @Composable
//    override fun RowScope.MessageComposerIntegrations(
//        state: MessageComposerState,
//        onAttachmentsClick: () -> Unit,
//        onCommandsClick: () -> Unit,
//    ) {
//        // Only keep the attachments button
//        IconButton(
//            modifier = Modifier
//                .size(48.dp)
//                .padding(12.dp),
//            onClick = onAttachmentsClick
//        ){
//            Icon( // 附件图标颜色
//                imageVector = Icons.Filled.Attachment,
//                contentDescription = "Send",
//                tint = if(isSystemInDarkTheme()) Color.White else LocalContentColor.current
//            )
//            // TODO：修改附件选项中的按钮，只保留发送图片
//        }
//    }
//
//    @Composable
//    override fun RowScope.MessageListHeaderCenterContent(
//        modifier: Modifier,
//        channel: Channel,
//        currentUser: User?,
//        typingUsers: List<User>,
//        messageMode: MessageMode,
//        onHeaderTitleClick: (Channel) -> Unit,
//        connectionState: ConnectionState,
//    ) {
//        // Your implementation for the message list header center content
//        val title = when (messageMode) {
//            MessageMode.Normal -> ChatTheme.channelNameFormatter.formatChannelName(channel, currentUser)
//            is MessageMode.MessageThread -> "" // 原本是资源型字符串，找不到，遂放弃
//        }
//
//        Column(
//            modifier = modifier
//                .height(IntrinsicSize.Max)
//                .clickable(
//                    interactionSource = remember { MutableInteractionSource() },
//                    indication = null,
//                    onClick = { onHeaderTitleClick(channel) },
//                ),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center,
//        ) {
//            Text(
//                modifier = Modifier.testTag("Stream_ChannelName"),
//                text = title,
//                style = ChatTheme.typography.title3Bold,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
//                color = ChatTheme.colors.textHighEmphasis,
//            )
//        }
//    }
//}
///**
// * 自动发送图片
// * */
//
//fun sendImage(context: Context, imagePath: Uri) {
//    val file = uriToFile(imagePath,context) ?: return
//    // 对话框架部分
//    val apiKey = "hz2zpsvk7e75"
//    val userToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoidGVzdFVzZXIifQ.LTIaVYRIcreLgqrl1ZuCxoTOuzvuRZyRxrP5Njscijk"
//    val offlinePluginFactory = StreamOfflinePluginFactory(appContext = context)
//    val statePluginFactory = StreamStatePluginFactory(config = StatePluginConfig(), appContext = context)
//    val client = ChatClient.Builder(apiKey, context)
//        .withPlugins(offlinePluginFactory,statePluginFactory) // 离线存储和缓存
//        .build()
//    client.connectUser(
//        user = User(
//            id = "testUser",
//            name = "Administrator",
//        ),
//        token = userToken
//    ).enqueue()
//
//    val channelClient = client.channel("messaging","general")
//    channelClient.sendImage(file).enqueue { result ->
//        if (result.isSuccess) {
//            // Successful upload, you can now attach this image
//            // to a message that you then send to a channel
//            val imageUrl = result.getOrThrow().file
//            val attachment = Attachment(
//                type = "image",
//                imageUrl = imageUrl,
//            )
//            val message = Message(
//                attachments = mutableListOf(attachment),
//            )
//            channelClient.sendMessage(message).enqueue { /* ... */ }
//        }
//    }
//}
//fun uriToFile(uri: Uri,context: Context) : File? {
//    if (uri.scheme == "file") {
//        return uri.path?.let { File(it) }
//    }
//    else if (uri.scheme == "content") {
//        val contentResolver: ContentResolver = context.contentResolver
//        try {
//            // 从 Uri 中获取输入流
//            val inputStream: InputStream? = contentResolver.openInputStream(uri)
//            if (inputStream != null) {
//                // 创建一个临时文件来保存内容
//                val file = File(context.cacheDir, "temp_file")
//                val outputStream = FileOutputStream(file)
//                val buffer = ByteArray(4 * 1024) // 4KB 缓冲区
//                var read: Int
//                while (inputStream.read(buffer).also { read = it } != -1) {
//                    outputStream.write(buffer, 0, read)
//                }
//                outputStream.flush()
//                outputStream.close()
//                inputStream.close()
//                return file
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//    return null
//}
//@Preview(showBackground = true)
//@Composable
//fun Test(){
//}