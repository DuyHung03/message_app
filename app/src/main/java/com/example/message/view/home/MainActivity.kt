package com.example.message.view.home

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.message.R
import com.example.message.adapter.MainPagerAdapter
import com.example.message.util.intent
import com.example.message.view.auth.LoginActivity
import com.example.message.view.home.fragments.HomeFragment
import com.example.message.view.home.fragments.SettingFragment
import com.example.message.viewmodel.ChatViewModel
import com.example.message.viewmodel.ChatViewModelFactory
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity(), SettingFragment.SettingFragmentCallback {


    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: MainPagerAdapter
    private val fragmentList = mutableListOf<Fragment>()
    private lateinit var chatViewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        chatViewModel =
            ViewModelProvider(this, ChatViewModelFactory(application))[ChatViewModel::class.java]

        fragmentList.add(HomeFragment())
        fragmentList.add(SettingFragment())

        initView()

        getFcmToken()
    }

    private fun getFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("TAG", token)
            val userRef =
                chatViewModel.db.collection("users")
                    .document(chatViewModel.currentUser.value?.email.toString())
            Log.d("TAG", "getFcmToken: $userRef")
            userRef.update("deviceToken", token)
        })
    }

    private fun initView() {

        viewPager = findViewById(R.id.viewPager)
        pagerAdapter = MainPagerAdapter(supportFragmentManager, lifecycle, fragmentList)
        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = 2

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavView)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> viewPager.currentItem = 0
                R.id.settingFragment -> viewPager.currentItem = 1
            }
            true
        }

        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomNavigationView.menu.getItem(position).isChecked = true
            }

        })
        viewPager.isUserInputEnabled = false
    }


    //called when log out button clicked in setting fragment
    override fun onFinishMainActivity() {
        this.finish()
        intent<LoginActivity>()
    }
}