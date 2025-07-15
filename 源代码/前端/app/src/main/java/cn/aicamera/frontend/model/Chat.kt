package cn.aicamera.frontend.model

// Chat功能有关数据结构

// 消息数据类
data class MessageRequest(val text: String)
data class Message(
    val text: String,
    val isUser: Boolean
)