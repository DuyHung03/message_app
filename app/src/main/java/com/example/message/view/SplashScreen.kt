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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private val splashTimeOut: Long = 1500 // 1.5 seconds
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Firebase.firestore.useEmulator("10.0.2.2", 8080)
        Firebase.auth.useEmulator("10.0.2.2", 9090)

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