package cn.aicamera.frontend.ui.component

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.aicamera.frontend.R
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.Platform.SHARE_IMAGE
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.framework.ShareSDK
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareSheet(
    showShareSheet : Boolean,
    onDismiss : ()->Unit,
    bitmap : Bitmap,
    vararg text : String?
){
    val context = LocalContext.current

    if(showShareSheet) {
        var sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 分享平台按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SharePlatformButton(
                        // TODO:替换成微信logo
                        icon = R.drawable.wechat_fill,
                        label = "微信朋友圈",
                        onClick = {
                            shareToPlatform(
                            context = context,
                            imageToShare = bitmap,
                            photoText = text.toString(),
                            shareTitle = "分享到微信朋友圈",
                            platformName = "WechatMoments"
                        ) }
                    )
                    SharePlatformButton(
                        // TODO:替换成微博logo
                        icon = R.drawable.sina,
                        label = "微博",
                        onClick = {
                            shareToPlatform(
                                context = context,
                                imageToShare = bitmap,
                                photoText = text.toString(),
                                shareTitle = "分享到微博",
                                platformName = "SinaWeibo"
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row{
                    // 取消按钮
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("取消")
                    }
                }
            }
        }
    }
}

@Composable
fun SharePlatformButton(
    icon: Int,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(Color.White, CircleShape)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
//                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, fontSize = 12.sp)
    }
}


private fun shareToPlatform(
    context: Context,
    imageToShare: Bitmap,
    photoText:String,
    platformName: String,
    shareTitle: String?) {
    // 暂存到缓存
    val cacheDir = context.cacheDir
    val file = File(cacheDir, "$platformName.jpg")
    file.outputStream().use {
        imageToShare.compress(Bitmap.CompressFormat.JPEG, 100, it)
    }

    val shareParams = Platform.ShareParams().apply {
        title = shareTitle
        text = photoText
        imagePath = file.absolutePath
        shareType = SHARE_IMAGE
    }
    val platform = ShareSDK.getPlatform(platformName)
    platform.share(shareParams)

    // 获取分享回调并删除缓存
    platform.setPlatformActionListener(object : PlatformActionListener {
        override fun onComplete(platform: Platform?, action: Int, data: HashMap<String, Any>?) {
            // 分享成功
            Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show()
        }

        override fun onError(platform: Platform?, action: Int, t: Throwable?) {
            // 分享失败
            Toast.makeText(context, "分享失败: ${t?.message}", Toast.LENGTH_SHORT).show()
        }

        override fun onCancel(platform: Platform?, action: Int) {
            // 分享取消
            Toast.makeText(context, "分享取消", Toast.LENGTH_SHORT).show()
        }
    })
    if(file.exists()){
        file.delete()
    }

}
@Preview
@Composable
fun test(){

}