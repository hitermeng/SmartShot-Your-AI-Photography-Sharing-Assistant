package cn.aicamera.frontend.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtils {
    fun uriToFile(uri: Uri, context: Context) : File? {
        if (uri.scheme == "file") {
            return uri.path?.let { File(it) }
        }
        else if (uri.scheme == "content") {
            val contentResolver: ContentResolver = context.contentResolver
            try {
                // 从 Uri 中获取输入流
                val inputStream: InputStream? = contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    // 创建一个临时文件来保存内容
                    val file = File(context.cacheDir, "temp_file_${sanitizeFileName(uri.path)}")
                    val outputStream = FileOutputStream(file)
                    val buffer = ByteArray(4 * 1024) // 4KB 缓冲区
                    var read: Int
                    while (inputStream.read(buffer).also { read = it } != -1) {
                        outputStream.write(buffer, 0, read)
                    }
                    outputStream.flush()
                    outputStream.close()
                    inputStream.close()
                    return file
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }
}

/**
 * 格式化uri的名称，使其合法
 */
fun sanitizeFileName(path: String?): String {
    return path?.replace(Regex("[^a-zA-Z0-9_.-]"), "_") ?: "temp_file"
}