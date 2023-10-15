package com.example.message

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.message.viewmodel.AuthViewModel
import com.example.message.viewmodel.AuthViewModelFactory

class SettingFragment : Fragment() {
    interface SettingFragmentCallback {
        fun onFinishMainActivity()
    }

    private lateinit var authViewModel: AuthViewModel

    private var callback: SettingFragmentCallback? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        authViewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(application = Application())
        )[AuthViewModel::class.java]

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        val logOutButton = view.findViewById<Button>(R.id.log_out_button)
        logOutButton.setOnClickListener {
            authViewModel.logOut()

            //called to finish mainActivity
            callback?.onFinishMainActivity()
        }

        return view
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