package com.example.message.view.auth

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.message.R
import com.example.message.databinding.ActivitySignUpBinding
import com.example.message.util.Resource
import com.example.message.util.addTextChangeListeners
import com.example.message.util.intent
import com.example.message.util.isValidEmail
import com.example.message.util.setupHideKeyboardOnTouchOutside
import com.example.message.util.toast
import com.example.message.util.validateTextInputLayouts
import com.example.message.viewmodel.ChatViewModel
import com.example.message.viewmodel.ChatViewModelFactory

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var signUpButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var loginLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        chatViewModel =
            ViewModelProvider(this, ChatViewModelFactory(application))[ChatViewModel::class.java]

        initViews()
        initControl()

    }

    private fun initViews() {
        signUpButton = findViewById(R.id.signup_button)
        progressBar = findViewById(R.id.progressBar)
        loginLink = findViewById(R.id.login_link)
    }

    private fun initControl() {
        signUpButton.setOnClickListener {
            signUp()
        }

        loginLink.setOnClickListener {
            intent<LoginActivity>()
            this.finish()
        }

        chatViewModel.loading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
        }

        addTextChangeListeners(
            listOf(
                binding.signupEmailLayout,
                binding.signupPasswordLayout,
                binding.signupConfirmPasswordLayout
            )
        )

        setupHideKeyboardOnTouchOutside(
            listOf(
                binding.signupEmailLayout,
                binding.signupPasswordLayout,
                binding.signupConfirmPasswordLayout
            ), binding.parentLayout
        )
    }

    private fun signUp() {
        val email = binding.signupEmailLayout
        val password = binding.signupPasswordLayout
        val confirmPassword = binding.signupConfirmPasswordLayout

        val fieldsToValid = listOf(
            Pair(email, "Please enter your username!"),
            Pair(password, "Please enter your password!"),
            Pair(confirmPassword, "Please re-enter your password")
        )

        if (validateTextInputLayouts(fieldsToValid)
            && isValidEmail(email.editText?.text.toString().trim(), this)
        ) {
            chatViewModel.signUp(
                email.editText?.text.toString().trim(),
                password.editText?.text.toString().trim()
            ) { authResult ->
                when (authResult) {
                    is Resource.Success -> {
                        intent<LoginActivity>()
                        this.finish()
                    }

                    is Resource.Failure -> {
                        val errorMessage = authResult.message
                        toast(errorMessage, this)
                    }
                }
            }
        }
    }
}