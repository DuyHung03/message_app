package com.example.message.model

import java.util.Date

data class ChatMessage(
    var senderId: String,
    var recipientId: String,
    var message: String,
    var time: Date,
)