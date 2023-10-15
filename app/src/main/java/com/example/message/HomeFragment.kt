package com.example.message

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.message.viewmodel.AuthViewModel
import com.example.message.viewmodel.AuthViewModelFactory

class HomeFragment : Fragment() {
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        authViewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(application = Application())
        )[AuthViewModel::class.java]
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

//        val user = authViewModel.auth.currentUser
//        if (user != null) {
//            val isNewUser = user.metadata?.creationTimestamp == user.metadata?.lastSignInTimestamp
//            if (isNewUser) {
//                startActivity(Intent(requireContext(), SettingFragment::class.java))
//            }
//        }

        val userButton = view.findViewById<LinearLayout>(R.id.userButton)
        userButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_settingFragment)
        }

        val displayName = view.findViewById<TextView>(R.id.displayName)

        authViewModel.currentUser.observe(requireActivity()) { res ->
            if (res!!.displayName != null) {
                displayName.text = res.displayName
            }
            displayName.text = res.email
        }

        return view
    }
}