package com.example.message.model

data class User(
    var userId: String,
    val email: String,
    val password: String,
    val displayName: String,
    val photoURL: String,
)