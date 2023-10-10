package com.example.message.view

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.message.R
import com.example.message.util.intent
import com.example.message.view.auth.LoginActivity
import com.example.message.viewmodel.AuthViewModel
import com.example.message.viewmodel.AuthViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var logOutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authViewModel =
            ViewModelProvider(
                this,
                AuthViewModelFactory(application)
            )[AuthViewModel::class.java]

        authViewModel.currentUser.observe(this) { res ->
            if (res != null) {
                val text = findViewById<TextView>(R.id.text)
                text.text = res.email
            } else {
                intent<LoginActivity>()
                this.finish()
            }
        }

        logOutButton = findViewById(R.id.log_out_button)
        logOutButton.setOnClickListener {
            authViewModel.logOut()
        }
    }
}