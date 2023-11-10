package com.example.message.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GetDateAndTime {
    companion object {

        fun getCurrentDateTime(): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return dateFormat.format(Date())
        }

        fun getCurrentDate(): String {
            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            return dateFormat.format(Date())
        }
//
//        fun getCurrentTime(): String {
//            val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
//            return dateFormat.format(Date())
//        }
    }
}