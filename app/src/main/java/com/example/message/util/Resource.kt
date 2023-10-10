package com.example.message.util

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Failure<out T>(val message: String, val exception: Exception? = null) :
        Resource<T>()
}