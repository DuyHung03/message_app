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
import com.bumptech.glide.Glide
import com.example.message.R
import com.example.message.adapter.UsersAdapter
import com.example.message.model.User
import com.example.message.view.chat.ChatActivity
import com.example.message.viewmodel.AuthViewModel
import com.example.message.viewmodel.AuthViewModelFactory
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var displayName: TextView
    private lateinit var avatar: CircleImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val userList: MutableList<User> = mutableListOf()
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProvider(
            this, AuthViewModelFactory(application = Application())
        )[AuthViewModel::class.java]


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

        lifecycleScope.launch { // Launch on the Main dispatcher
            val currentUser =
                authViewModel.currentUser.value // Get the current user on the Main dispatcher

            //get displayName from currentUser
            val displayText = withContext(Dispatchers.IO) {
                currentUser?.displayName?.takeIf { it.isNotBlank() } ?: currentUser?.email
            }
            displayName.text = displayText

            //set avatar image
            withContext(Dispatchers.IO) {
                //check if photoUrl in currentUser is not null
                if (currentUser?.photoUrl != null) {
                    //use Glide to load photo
                    val photo = withContext(Dispatchers.IO) {
                        Glide.with(this@HomeFragment)
                            .load(currentUser.photoUrl)
                            .override(250, 250)
                            .error(R.mipmap.brg)
                            .submit()
                            .get()
                    }

                    withContext(Dispatchers.Main) {
                        //set avatar image
                        avatar.setImageDrawable(photo)
                    }
                }
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                getUsersList()
            }

            swipeRefreshLayout.setOnRefreshListener {
                getUsersList()
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun getUsersList() {
        userList.clear()
        authViewModel.db.collection("users")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (doc in querySnapshot) {
                    val userId = doc.getString("userId")
                    val displayName = doc.getString("displayName")
                    val email = doc.getString("email")

                    if (userId != authViewModel.currentUser.value?.uid) {
                        val user = User(userId, email, displayName)
                        userList.add(user)
                    }
                }
                //recyclerView
                recyclerView.layoutManager = LinearLayoutManager(context)
                val adapter = UsersAdapter(userList) { user ->
                    val intent = Intent(requireContext(), ChatActivity::class.java)
                    intent.putExtra("email", user.email)
                    startActivity(intent)
                }
                recyclerView.adapter = adapter
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                Log.d("TAG", "onCreateView: ", e)
                progressBar.visibility = View.GONE
            }
    }
}