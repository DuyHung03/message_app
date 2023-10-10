package com.example.message.util

import android.content.Context
import java.util.regex.Pattern

fun isValidEmail(email: String, context: Context): Boolean {
    val emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$"
    val pattern = Pattern.compile(emailPattern)
    val matcher = pattern.matcher(email)
    if (!matcher.matches()) {
        toast("Email is badly formatted", context)
        return false
    }
    return true
}