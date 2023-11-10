package com.example.message.view.chat

import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.example.message.R
import com.example.message.databinding.ActivityChatBinding
import com.example.message.model.Message
import com.example.message.model.User
import com.example.message.util.GlideImageLoader
import com.example.message.viewmodel.ChatViewModel
import com.example.message.viewmodel.ChatViewModelFactory
import com.google.firebase.auth.FirebaseUser

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var toolbar: Toolbar
    private lateinit var avatar: ImageView
    private lateinit var displayName: TextView
    private lateinit var glideImageLoader: GlideImageLoader
    private lateinit var sendButton: ImageView
    private lateinit var editTextMessage: EditText
    private lateinit var chatViewModel: ChatViewModel
    private var recipient: User? = null
    private var currentUser: FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_chat)
        setContentView(binding.root)

        chatViewModel =
            ViewModelProvider(this, ChatViewModelFactory(application))[ChatViewModel::class.java]

        glideImageLoader = GlideImageLoader(this)

        initView()
        initControl()
        setUserInfo()
    }

    private fun setUserInfo() {
        intent?.let {
            recipient =
                intent.extras?.get("user_object") as? User

            if (recipient != null) {
                binding.tv.text = recipient.toString()
                displayName.text = recipient!!.displayName ?: recipient!!.email
                glideImageLoader.load(
                    recipient!!.photoURL,
                    avatar,
                    R.drawable.ic_user_foreground,
                    R.drawable.ic_user_foreground
                )
            }
        }
    }

    private fun initView() {
        toolbar = findViewById(R.id.chat_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false) //hide default title

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_left_arrow_foreground)

        currentUser = chatViewModel.currentUser.value

        avatar = toolbar.findViewById(R.id.avatar)
        displayName = toolbar.findViewById(R.id.displayName)

        sendButton = binding.sendButton
        editTextMessage = binding.edtMessage
    }


    private fun initControl() {
        sendButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun sendMessage() {
        val text = editTextMessage.text.trim().toString()
        if (text.isNotEmpty()) {
            val message = Message(currentUser!!.uid, recipient!!.userId!!, text)

            chatViewModel.addMessageToDb(message)

        }
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}