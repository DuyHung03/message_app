package com.example.message.view.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.message.R
import com.example.message.util.intent
import com.example.message.view.auth.LoginActivity
import com.example.message.view.home.fragments.SettingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), SettingFragment.SettingFragmentCallback {

//    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        authViewModel = ViewModelProvider(
//            this, AuthViewModelFactory(application)
//        )[AuthViewModel::class.java]

//        authViewModel.currentUser.observe(this) { res ->
//            if (res == null) {
//                intent<LoginActivity>()
//                Log.d("TAG", "Log out")
//                this.finish()
//            }
//        }

        initView()
    }

    private fun initView() {
        //setup bottom navigation
        val navController = findNavController(R.id.nav_host_fragment)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavView)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }

    //called when log out button clicked in setting fragment
    override fun onFinishMainActivity() {
        this.finish()
        intent<LoginActivity>()
    }
}