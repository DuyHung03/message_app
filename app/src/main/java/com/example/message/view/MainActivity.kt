package com.example.message.view

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.message.R
import com.example.message.util.intent
import com.example.message.view.auth.LoginActivity
import com.example.message.viewmodel.AuthViewModel
import com.example.message.viewmodel.AuthViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

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
//                val text = findViewById<TextView>(R.id.text)
//                text.text = res.email
            } else {
                intent<LoginActivity>()
                this.finish()
            }
        }

        initView()

    }

    private fun initView() {
        val navController = findNavController(R.id.nav_host_fragment)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavView)

        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }
}