package com.example.message.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var userId: String?,
    val email: String?,
    val password: String? = null,
    val displayName: String? = null,
    val photoURL: String? = null,
    val isPasswordUpdated: Boolean = false,
    val isNewUser: Boolean = true,
    val deviceToken: String? = null,
) : Parcelable