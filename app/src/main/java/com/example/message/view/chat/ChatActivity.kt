package com.example.message.view.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.message.R
import com.example.message.adapter.MessageAdapter
import com.example.message.databinding.ActivityChatBinding
import com.example.message.model.Message
import com.example.message.model.NotificationData
import com.example.message.model.PushNotification
import com.example.message.model.User
import com.example.message.notifacation.NotificationAPI
import com.example.message.util.GlideImageLoader
import com.example.message.util.setupHideKeyboardOnTouchOutside
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
    private lateinit var recyclerView: RecyclerView
    private var recipient: User? = null
    private var currentUser: FirebaseUser? = null
    private var messageList: List<Message> = mutableListOf()
    private lateinit var toToken: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_chat)
        setContentView(binding.root)

        chatViewModel =
            ViewModelProvider(this, ChatViewModelFactory(application))[ChatViewModel::class.java]

        glideImageLoader = GlideImageLoader(this)

        initView()
        setUserInfo()
        initControl()

//        chatViewModel.db.collection("users").document(recipient!!.email!!).get()
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    toToken = task.result.getString("deviceToken")
//                    Log.d("TAG", "initView: $toToken")
//                }
//            }
    }

    private fun setUserInfo() {
        intent?.let {
            recipient =
                intent.extras?.get("user_object") as? User

            if (recipient != null) {
                displayName.text = recipient!!.displayName ?: recipient!!.email
                toToken = recipient!!.deviceToken!!
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
        recyclerView = findViewById(R.id.recyclerView)

        toolbar = findViewById(R.id.chat_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false) //hide default title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.themedContext

        currentUser = chatViewModel.currentUser.value

        avatar = toolbar.findViewById(R.id.avatar)
        displayName = toolbar.findViewById(R.id.displayName)

        sendButton = binding.sendButton
        editTextMessage = binding.edtMessage
    }

    private fun initControl() {

        setupHideKeyboardOnTouchOutside(
            listOf(
                binding.edtLayout
            ), binding.recyclerView
        )

        sendButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun sendMessage() {
        val text = editTextMessage.text.trim().toString()
        if (text.isNotEmpty()) {
            val message = Message(currentUser!!.uid, recipient!!.userId!!, text)

            chatViewModel.addMessageToDb(currentUser!!.uid, recipient!!.userId!!, message)


            editTextMessage.text.clear()

            Log.d("TAG", "sendMessage: $toToken + ${currentUser!!.email}")


//            send notification
            NotificationAPI.retrofitService.postNotification(
                PushNotification(
                    NotificationData(
                        currentUser!!.email!!,
                        text
                    ),
                    toToken
                )
            )
        }
    }

    override fun onStart() {
        super.onStart()
        fetchMessages()
    }

    override fun onResume() {
        super.onResume()
        fetchMessages()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchMessages() {
        chatViewModel.fetchMessages(currentUser!!.uid, recipient!!.userId!!) { snapshot, e ->
            if (e != null) {
                Log.d("TAG", "fetchMessages: $e")
            }

            if (snapshot != null) {
                messageList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Message::class.java)
                }

                setupRecyclerView(messageList, currentUser!!.uid)

            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView(messageList: List<Message>, currentUserID: String) {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        layoutManager.isSmoothScrollbarEnabled = true
        recyclerView.layoutManager = layoutManager

        val adapter = MessageAdapter(messageList, currentUserID)
        recyclerView.adapter = adapter
        recyclerView.adapter?.notifyDataSetChanged()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy < 0 && layoutManager.findFirstVisibleItemPosition() == 0) {
                    Log.d("TAG", "onScrolled: $dy")
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            this.finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}