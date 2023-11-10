package com.example.message.view.home.fragments

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.message.R
import com.example.message.adapter.UsersAdapter
import com.example.message.model.User
import com.example.message.util.GlideImageLoader
import com.example.message.view.chat.ChatActivity
import com.example.message.viewmodel.ChatViewModelFactory
import com.example.message.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var displayName: TextView
    private lateinit var avatar: CircleImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val userList: MutableList<User> = mutableListOf()
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var currentUser: FirebaseUser? = null
    private lateinit var glideImageLoader: GlideImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chatViewModel = ViewModelProvider(
            this, ChatViewModelFactory(application = Application())
        )[ChatViewModel::class.java]

        glideImageLoader = GlideImageLoader(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        displayName = view.findViewById(R.id.displayName)
        avatar = view.findViewById(R.id.avatar)
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        progressBar.visibility = View.VISIBLE

        setUserInfo()
        setupSwipeRefreshListener()


        return view
    }

    override fun onResume() {
        super.onResume()
        loadUserAvatar()
        fetchUsersList()
    }

    private fun setupSwipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener {
            fetchUsersList()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setUserInfo() {
        lifecycleScope.launch {
            currentUser =
                chatViewModel.currentUser.value // Get the current user on the Main dispatcher

            //get displayName from currentUser
            val displayText = withContext(Dispatchers.IO) {
                currentUser?.displayName?.takeIf { it.isNotBlank() } ?: currentUser?.email
            }
            withContext(Dispatchers.Main) {
                displayName.text = displayText
            }
        }
    }

    private fun loadUserAvatar() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                //check if photoUrl in currentUser is not null
                if (currentUser?.photoUrl != null) {
                    //use Glide to load photo
                    glideImageLoader.load(
                        currentUser?.photoUrl.toString(),
                        avatar,
                        R.drawable.ic_user_foreground,
                        R.drawable.ic_user_foreground
                    )
                }
            }
        }
    }

    private fun fetchUsersList() {
        lifecycleScope.launch(Dispatchers.IO) {
            userList.clear()
            chatViewModel.db.collection("users")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (doc in querySnapshot) {
                        val userId = doc.getString("userId")
                        val displayName = doc.getString("displayName")
                        val email = doc.getString("email")
                        val photoUrl = doc.getString("photoURL")

                        if (userId != chatViewModel.currentUser.value?.uid) {
                            val user = User(userId, email, displayName, photoUrl)
                            userList.add(user)
                        }
                    }
                    //recyclerView
                    setupRecyclerView()
                }

                .addOnFailureListener { e ->
                    Log.e("TAG", "onCreateView: ", e)
                    progressBar.visibility = View.GONE
                }
        }
    }

    private fun setupRecyclerView() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                recyclerView.layoutManager = LinearLayoutManager(context)
                val adapter = UsersAdapter(userList, requireContext()) { user ->
                    startChatActivity(user)
                }
                recyclerView.adapter = adapter
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun startChatActivity(user: User) {
        val intent = Intent(requireContext(), ChatActivity::class.java)
        intent.putExtra("user_object", user)
        startActivity(intent)
    }
}