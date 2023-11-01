package com.example.message.view.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.message.R
import com.example.message.adapter.MainPagerAdapter
import com.example.message.util.intent
import com.example.message.view.auth.LoginActivity
import com.example.message.view.home.fragments.HomeFragment
import com.example.message.view.home.fragments.SettingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), SettingFragment.SettingFragmentCallback {


    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: MainPagerAdapter
    private val fragmentList = mutableListOf<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        fragmentList.add(HomeFragment())
        fragmentList.add(SettingFragment())

        initView()
    }

    private fun initView() {

        viewPager = findViewById(R.id.viewPager)
        pagerAdapter = MainPagerAdapter(supportFragmentManager, lifecycle, fragmentList)
        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = 1

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavView)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> viewPager.currentItem = 0
                R.id.settingFragment -> viewPager.currentItem = 1
            }
            true
        }
    }

    //called when log out button clicked in setting fragment
    override fun onFinishMainActivity() {
        this.finish()
        intent<LoginActivity>()
    }
}