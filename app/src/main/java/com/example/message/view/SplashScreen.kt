package com.example.message.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.message.R
import com.example.message.util.intent
import com.example.message.view.auth.LoginActivity
import com.example.message.view.home.MainActivity
import com.example.message.viewmodel.ChatViewModel
import com.example.message.viewmodel.ChatViewModelFactory

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private val splashTimeOut: Long = 500 // 0.5 seconds
    private lateinit var chatViewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        Firebase.firestore.useEmulator("10.0.2.2", 8080)
//        Firebase.auth.useEmulator("10.0.2.2", 9090)

        setContentView(R.layout.activity_splash_screen)

        chatViewModel =
            ViewModelProvider(this, ChatViewModelFactory(application))[ChatViewModel::class.java]

        if (intent != null) {
            val email = intent.extras?.getString("email")
        }

        Handler(Looper.getMainLooper()).postDelayed({
            chatViewModel.currentUser.observe(this) { res ->
                if (res != null) {
                    intent<MainActivity>()
                    finish()
                } else {
                    intent<LoginActivity>()
                    finish()
                }
            }
        }, splashTimeOut)
    }
}