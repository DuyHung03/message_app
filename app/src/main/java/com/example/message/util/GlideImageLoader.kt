package com.example.message.util

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class GlideImageLoader(private val context: Context) {
    @SuppressLint("CheckResult")
    fun load(
        imgUrl: String?,
        imageView: ImageView,
        placeholder: Int,
        error: Int?,
        isCacheEnabled: Boolean = true,
    ) {
        if (imgUrl.isNullOrBlank()) {
            return
        }

        val glideRequest = Glide.with(context)
            .load(imgUrl)
            .override(250, 250)
            .placeholder(placeholder)
            .error(error)

        if (!isCacheEnabled) {
            glideRequest.diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
        }

        glideRequest.into(imageView)
    }
}