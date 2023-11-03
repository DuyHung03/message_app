package com.example.message.model

import java.util.Date

data class Message(
    var conversationId: String,
    var messageId: String,
    var senderId: String,
    var recipientId: String,
    var message: String,
    var time: Date,
)