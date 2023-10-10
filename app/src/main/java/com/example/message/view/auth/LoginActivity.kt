package com.example.message.view.auth

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.message.R
import com.example.message.databinding.ActivityLoginBinding
import com.example.message.util.Resource
import com.example.message.util.addTextChangeListeners
import com.example.message.util.intent
import com.example.message.util.isValidEmail
import com.example.message.util.setupHideKeyboardOnTouchOutside
import com.example.message.util.toast
import com.example.message.util.validateTextInputLayouts
import com.example.message.view.MainActivity
import com.example.message.viewmodel.AuthViewModel
import com.example.message.viewmodel.AuthViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel

    private lateinit var loginButton: Button
    private lateinit var signUpLink: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        authViewModel = ViewModelProvider(
            this, AuthViewModelFactory(application)
        )[AuthViewModel::class.java]

        initView()
        initControl()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {

        return super.dispatchTouchEvent(ev)
    }

    private fun initView() {
        loginButton = findViewById(R.id.signin_button)
        signUpLink = findViewById(R.id.signup_link)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun initControl() {

        loginButton.setOnClickListener {
            emailLogin()
        }

        signUpLink.setOnClickListener {
            intent<SignUpActivity>()
        }

        authViewModel.loading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
        }

        addTextChangeListeners(
            listOf(
                binding.signinEmailLayout,
                binding.signinPasswordLayout
            )
        )

        setupHideKeyboardOnTouchOutside(
            listOf(
                binding.signinEmailLayout,
                binding.signinPasswordLayout
            ), binding.parentLayout
        )
    }


    private fun emailLogin() {
        val email = binding.signinEmailLayout
        val password = binding.signinPasswordLayout

        val fieldsToValidate = listOf(
            Pair(email, "Please enter your username!"),
            Pair(password, "Please enter your password!"),
        )

        if (
            validateTextInputLayouts(fieldsToValidate)
            && isValidEmail(email.editText?.text.toString(), this)
        ) {
            authViewModel.logIn(
                email.editText?.text.toString().trim(),
                password.editText?.text.toString().trim()
            ) { authResult ->
                when (authResult) {
                    is Resource.Success -> {
                        authViewModel.currentUser.value = authResult.data
                        intent<MainActivity>()
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