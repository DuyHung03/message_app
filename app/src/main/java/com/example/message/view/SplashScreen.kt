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
import com.example.message.viewmodel.AuthViewModel
import com.example.message.viewmodel.AuthViewModelFactory

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private val splashTimeOut: Long = 1500 // 1.5 seconds
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        authViewModel =
            ViewModelProvider(this, AuthViewModelFactory(application))[AuthViewModel::class.java]

        Handler(Looper.getMainLooper()).postDelayed({
            authViewModel.currentUser.observe(this) { res ->
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