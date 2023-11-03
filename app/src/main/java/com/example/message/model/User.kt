package com.example.message.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var userId: String?,
    val email: String?,
    val displayName: String? = null,
    val photoURL: String? = null,
) : Parcelable