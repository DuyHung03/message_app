package com.example.message.model

import com.example.message.util.GenerateRandomID
import com.example.message.util.GetDateAndTime

data class Message(
    var senderId: String = "",
    var recipientId: String = "",
    var message: String = "",
    var time: String = GetDateAndTime.getCurrentDateTime(),
    var messageId: String = GenerateRandomID.generateRandomId(20),
)