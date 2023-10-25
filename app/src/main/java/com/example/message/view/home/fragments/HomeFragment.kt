package com.example.message.view.home.fragments

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.message.R
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

        lifecycleScope.launch { // Launch on the Main dispatcher
            val currentUser =
                authViewModel.currentUser.value // Get the current user on the Main dispatcher
            val displayText = withContext(Dispatchers.IO) {
                currentUser?.displayName?.takeIf { it.isNotBlank() } ?: currentUser?.email
            }

            val photo = withContext(Dispatchers.IO) {
                Glide.with(this@HomeFragment)
                    .load(currentUser?.photoUrl)
                    .override(250, 250)
                    .submit()
                    .get()
            }

            withContext(Dispatchers.Main) {
                displayName.text = displayText
                avatar.setImageDrawable(photo)
            }
        }


        return view
    }

//    private fun showDialogUpdateUserInfo() {
//        val layResId = R.layout.reset_password_dialog
//        val dialog = BottomSheetDialogComponent(layResId)
//
//        dialog.show(parentFragmentManager, dialog.tag)
//    }
}