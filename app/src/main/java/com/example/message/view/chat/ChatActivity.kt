package com.example.message.view.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.message.R
import com.example.message.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_chat)
        setContentView(binding.root)

        val intent = intent

        val email = intent.getStringExtra("email")

        binding.textView4.text = email
    }
}