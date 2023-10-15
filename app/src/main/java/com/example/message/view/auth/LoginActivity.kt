package com.example.message.view.auth

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
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
    private lateinit var forgetPassword: TextView
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
        forgetPassword = findViewById(R.id.forgot_password)
    }

    private fun initControl() {

        loginButton.setOnClickListener {
            emailLogin()
        }

        signUpLink.setOnClickListener {
            intent<SignUpActivity>()
        }

        forgetPassword.setOnClickListener {
            openDialog()
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

    private fun openDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.reset_password_dialog)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val sendButton = dialog.findViewById<Button>(R.id.sendEmailResetButton)
        val cancelButton = dialog.findViewById<Button>(R.id.cancelDialogButton)
        val loading = dialog.findViewById<ProgressBar>(R.id.progressBar)

        sendButton.setOnClickListener {
            loading.visibility = View.VISIBLE
            val email = dialog.findViewById<EditText>(R.id.edtEmailReset)

            if (email.text.toString().trim().isNotEmpty() || isValidEmail(
                    email.text.toString().trim(), this
                )
            ) {
                authViewModel.sendPasswordResetEmail(
                    email.text.toString().trim()
                ) { success, message ->
                    loading.visibility = View.GONE
                    if (success) {
                        dialog.dismiss()
                        notifyDialog("Send success", message)
                    } else {
                        dialog.dismiss()
                        notifyDialog("Send Failed", message)
                    }
                }
            } else {
                loading.visibility = View.GONE
                toast("Please enter a valid email address.", this)
            }
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun notifyDialog(titleDialog: String, messageDialog: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.notify_dialog)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val title = dialog.findViewById<TextView>(R.id.tvTitleDialog)
        val message = dialog.findViewById<TextView>(R.id.tvMessage)
        val okButton = dialog.findViewById<Button>(R.id.okButton)

        title.text = titleDialog
        message.text = messageDialog

        okButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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