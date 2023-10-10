package com.example.message.util

import android.content.Context
import android.content.Intent

inline fun <reified T : Any> Context.intent() {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}