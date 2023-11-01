package com.example.message.model

import android.net.Uri

data class User(
    var userId: String?,
    val email: String?,
    val displayName: String? = null,
    val photoURL: Uri? = null,
)