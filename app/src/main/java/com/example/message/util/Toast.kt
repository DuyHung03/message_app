package com.example.message.util

import android.content.Context
import android.widget.Toast

fun toast(text: String, context: Context) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}