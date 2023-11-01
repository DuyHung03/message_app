package com.example.message.view.home.fragments

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.message.R
import com.example.message.util.toast
import com.example.message.view.home.EditProfileActivity
import com.example.message.viewmodel.AuthViewModel
import com.example.message.viewmodel.AuthViewModelFactory
import com.github.dhaval2404.imagepicker.ImagePicker
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
    private lateinit var pickAvatarBtn: ImageView
    private var imageUploadJob: Job? = null
    private lateinit var displayName: TextView
    private lateinit var editProfile: Button


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
        pickAvatarBtn = view.findViewById(R.id.pickAvatarButton)
        displayName = view.findViewById(R.id.displayName)
        editProfile = view.findViewById(R.id.editProfileButton)

        return view
    }

    override fun onResume() {
        super.onResume()

        initControl()

        lifecycleScope.launch {

            val currentUser =
                authViewModel.currentUser.value // Get the current user on the Main dispatcher

            val displayText = withContext(Dispatchers.IO) {
                currentUser?.displayName?.takeIf { it.isNotBlank() } ?: currentUser?.email
            }
            displayName.text = displayText

            withContext(Dispatchers.IO) {
                if (currentUser?.photoUrl != null) {
                    Log.d("TAG", "onCreateView: ${currentUser.photoUrl}")
                    val photo = withContext(Dispatchers.IO) {
                        Glide.with(this@SettingFragment).load(currentUser.photoUrl)
                            .override(250, 250).error(R.mipmap.brg).submit().get()
                    }

                    withContext(Dispatchers.Main) {
                        avatar.setImageDrawable(photo)
                    }
                }

            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION") super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                imageUploadJob = lifecycleScope.launch {
                    authViewModel.uploadImageToStorage(uri) { imgUrl, error ->
                        if (imgUrl != null) {
                            //update user photo
                            authViewModel.updateUserProfile("", imgUrl)

                            //set avatar
                            lifecycleScope.launch {
                                // Load the photo from the network using Dispatchers.IO
                                val photo = withContext(Dispatchers.IO) {
                                    Glide.with(this@SettingFragment).load(imgUrl).override(250, 250)
                                        .error(R.mipmap.brg).submit().get()
                                }
                                // Set the photo on the avatar view on the main thread
                                withContext(Dispatchers.Main) {
                                    avatar.setImageDrawable(photo)
                                }
                            }

                            //update image url in db
                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) {
                                    val userRef = authViewModel.db.collection("users")
                                        .document(authViewModel.currentUser.value!!.email.toString())
                                    authViewModel.updateDocument(userRef, "photoURL", imgUrl)
                                }
                            }

                        } else {
                            toast(error, requireContext())
                        }
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

        editProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
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
