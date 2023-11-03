package com.example.message.view.chat

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.message.R
import com.example.message.databinding.ActivityChatBinding
import com.example.message.model.User
import com.example.message.util.GlideImageLoader

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var toolbar: Toolbar
    private lateinit var avatar: ImageView
    private lateinit var displayName: TextView
    private lateinit var glideImageLoader: GlideImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_chat)
        setContentView(binding.root)

        glideImageLoader = GlideImageLoader(this)

        toolbar = findViewById(R.id.chat_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false) //hide default title

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_left_arrow_foreground)


        avatar = toolbar.findViewById(R.id.avatar)
        displayName = toolbar.findViewById(R.id.displayName)

        intent?.let {
            val user =
                intent.extras?.get("user_object") as? User

            if (user != null) {
                binding.tv.text = user.toString()
                displayName.text = user.displayName ?: user.email
                glideImageLoader.load(
                    user.photoURL,
                    avatar,
                    R.drawable.ic_user_foreground,
                    R.drawable.ic_user_foreground
                )
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}