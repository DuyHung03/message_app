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
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.message.R
import com.example.message.util.GlideImageLoader
import com.example.message.util.toast
import com.example.message.view.home.EditProfileActivity
import com.example.message.viewmodel.ChatViewModel
import com.example.message.viewmodel.ChatViewModelFactory
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingFragment : Fragment() {
    interface SettingFragmentCallback {
        fun onFinishMainActivity()
    }

    private lateinit var chatViewModel: ChatViewModel
    private lateinit var avatar: CircleImageView
    private var callback: SettingFragmentCallback? = null
    private lateinit var logOutButton: Button
    private lateinit var pickAvatarBtn: ImageView
    private lateinit var displayName: TextView
    private lateinit var editProfile: Button
    private var currentUser: FirebaseUser? = null
    private lateinit var glideImageLoader: GlideImageLoader


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatViewModel = ViewModelProvider(
            this, ChatViewModelFactory(application = Application())
        )[ChatViewModel::class.java]

        glideImageLoader = GlideImageLoader(requireContext())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SettingFragmentCallback) {
            callback = context
        } else {
            throw RuntimeException("$context must implement SettingFragmentCallback")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        logOutButton = view.findViewById(R.id.log_out_button)
        avatar = view.findViewById(R.id.avatarImage)
        pickAvatarBtn = view.findViewById(R.id.pickAvatarButton)
        displayName = view.findViewById(R.id.displayName)
        editProfile = view.findViewById(R.id.editProfileButton)

        initControl()

        return view
    }

    private fun initControl() {
        logOutButton.setOnClickListener {
            chatViewModel.logOut()
            callback?.onFinishMainActivity()
        }

        editProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        pickAvatarBtn.setOnClickListener { pickAvatar() }
    }

    override fun onResume() {
        super.onResume()

        setUserInfo()
    }

    private fun setUserInfo() {
        lifecycleScope.launch {
            currentUser = chatViewModel.currentUser.value
            val displayText = withContext(Dispatchers.IO) {
                currentUser?.displayName?.takeIf { it.isNotBlank() } ?: currentUser?.email
            }

            withContext(Dispatchers.Main) {
                displayName.text = displayText

                if (currentUser?.photoUrl != null) {
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


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION") super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                lifecycleScope.launch {
                    chatViewModel.uploadImageToStorage(uri) { imgUrl, error ->
                        if (imgUrl != null) {
                            //update user photoUrl
                            chatViewModel.updateUserProfile("", imgUrl)

                            //set avatar immediately
                            setAvatar(imgUrl)

                            //update image url in db
                            updateImageUrlInDb(imgUrl)

                        } else {
                            toast(error, requireContext())
                        }
                    }
                }
            }
        }
    }

    private fun setAvatar(imgUrl: String) {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                glideImageLoader.load(
                    imgUrl,
                    avatar,
                    R.drawable.ic_user_foreground,
                    R.drawable.ic_user_foreground
                )
            }
        }
    }

    private fun updateImageUrlInDb(imgUrl: String) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val userRef = chatViewModel.db.collection("users")
                    .document(chatViewModel.currentUser.value!!.email.toString())
                chatViewModel.updateDocument(userRef, "photoURL", imgUrl)
            }
        }
    }


    private fun pickAvatar() {
        ImagePicker.with(this).galleryOnly().crop().start()
    }
}
