package com.example.message.view.home.fragments

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.message.R
import com.example.message.viewmodel.AuthViewModel
import com.example.message.viewmodel.AuthViewModelFactory
import com.github.dhaval2404.imagepicker.ImagePicker
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingFragment : Fragment() {
    interface SettingFragmentCallback {
        fun onFinishMainActivity()
    }

    private lateinit var authViewModel: AuthViewModel
    private lateinit var avatar: CircleImageView
    private var callback: SettingFragmentCallback? = null
    private lateinit var logOutButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var pickAvatarBtn: ImageView


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
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        logOutButton = view.findViewById(R.id.log_out_button)
        avatar = view.findViewById(R.id.avatarImage)
        progressBar = view.findViewById(R.id.progressBar)
        pickAvatarBtn = view.findViewById(R.id.pickAvatarButton)


//        lifecycleScope.launch(Dispatchers.Main) {
//            authViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
//                progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
//            }
//        }

        lifecycleScope.launch(Dispatchers.Default) {
            initControl()
        }



        lifecycleScope.launch {

            val currentUser =
                authViewModel.currentUser.value // Get the current user on the Main dispatcher

            val imageLoader = Glide.with(this@SettingFragment)
            // Get the user's photo URL

            val photoUrl = currentUser?.photoUrl

            // Load the photo from the network using Dispatchers.IO
            val photo = withContext(Dispatchers.IO) {
                imageLoader.load(photoUrl)
                    .override(250, 250)
                    .submit()
                    .get()
            }

            // Set the photo on the avatar view on the main thread
            withContext(Dispatchers.Main) {
                avatar.setImageDrawable(photo)
            }
        }

        return view
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION") super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                authViewModel.updateUserProfile("", uri.toString())
                val userRef = authViewModel.db.collection("users")
                    .document(authViewModel.currentUser.value!!.email.toString())
                authViewModel.updateDocument(userRef, "photoURL", uri)
                lifecycleScope.launch {
                    // Load the photo from the network using Dispatchers.IO
                    val photo = withContext(Dispatchers.IO) {
                        Glide.with(this@SettingFragment).load(uri)
                            .override(250, 250)
                            .submit()
                            .get()
                    }
                    // Set the photo on the avatar view on the main thread
                    withContext(Dispatchers.Main) {
                        avatar.setImageDrawable(photo)
                    }
                }
            }
        }
    }

    private fun initControl() {
        logOutButton.setOnClickListener {
            authViewModel.logOut()
            callback?.onFinishMainActivity()
        }

        pickAvatarBtn.setOnClickListener { pickAvatar() }
    }

    private fun pickAvatar() {
        ImagePicker.with(this).galleryOnly().crop().start()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SettingFragmentCallback) {
            callback = context
        } else {
            throw RuntimeException("$context must implement SettingFragmentCallback")
        }
    }
}
