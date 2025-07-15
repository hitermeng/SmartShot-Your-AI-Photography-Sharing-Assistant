package cn.aicamera.frontend.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

// 来源：https://blog.csdn.net/Tobey_r1/article/details/131414236
/**
 * 选择一张照片
 */
class SelectPicture : ActivityResultContract<Unit?, Uri?>() {

    private var context: Context? = null

    override fun createIntent(context: Context, input: Unit?): Intent {
        this.context = context
        return Intent(Intent.ACTION_PICK).setType("image/*")
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri?{
        return intent?.data
    }
}

/**
 * 选择多张照片
 */
class SelectMultiplePicture : ActivityResultContract<Unit?, List<Uri>?>() {

    private var context: Context? = null

    override fun createIntent(context: Context, input: Unit?): Intent {
        this.context = context
        return Intent(Intent.ACTION_PICK).setType("image/*").putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): List<Uri>? {
        return if (resultCode == Activity.RESULT_OK && intent != null) {
            val uriList = mutableListOf<Uri>()
            // 获取单张图片
            intent.data?.let { uri ->
                uriList.add(uri)
            }
            // 获取多张图片
            intent.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    clipData.getItemAt(i).uri?.let { uri ->
                        uriList.add(uri)
                    }
                }
            }
            uriList
        } else if(resultCode == Activity.RESULT_CANCELED) {
            return null
        }
        else {
            emptyList()
        }
    }
}

