package com.example.message.notifacation

import com.example.message.fcm.ValueConstant.Companion.BASE_URL
import com.example.message.fcm.ValueConstant.Companion.CONTENT_TYPE
import com.example.message.fcm.ValueConstant.Companion.SERVER_KEY
import com.example.message.model.PushNotification
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Build the Moshi object with Kotlin adapter factory that Retrofit will be using.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * The Retrofit object with the Moshi converter.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface NotificationAPIService {
    @Headers(
        "Content-Type: $CONTENT_TYPE",
        "Authorization: key=$SERVER_KEY"
    )
    @POST("fcm/send")
     fun postNotification(
        @Body notification: PushNotification,
    ): Response<ResponseBody>
}

object NotificationAPI {
    val retrofitService: NotificationAPIService by lazy { retrofit.create(NotificationAPIService::class.java) }
}