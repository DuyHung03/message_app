package com.example.message.view.auth

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
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
import com.example.message.view.home.MainActivity
import com.example.message.viewmodel.AuthViewModel
import com.example.message.viewmodel.AuthViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

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
                binding.signinEmailLayout, binding.signinPasswordLayout
            )
        )

        setupHideKeyboardOnTouchOutside(
            listOf(
                binding.signinEmailLayout, binding.signinPasswordLayout
            ), binding.parentLayout
        )
    }

    private fun openDialog() {
        val dialog = BottomSheetDialog(this)

        dialog.setContentView(R.layout.reset_password_dialog)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val sendButton = dialog.findViewById<Button>(R.id.sendButton)!!
        val cancelButton = dialog.findViewById<ImageView>(R.id.cancelDialogButton)!!
        val loading = dialog.findViewById<ProgressBar>(R.id.progressBar)!!

        sendButton.setOnClickListener {
            loading.visibility = View.VISIBLE
            val email: TextInputEditText? = dialog.findViewById(R.id.edtEmailReset)

            if (email?.text.toString().trim().isNotEmpty() || isValidEmail(
                    email?.text.toString().trim(), this
                )
            ) {
                authViewModel.sendPasswordResetEmail(
                    email?.text.toString().trim()
                ) { success, message ->
                    loading.visibility = View.GONE
                    if (success) {
                        notifyDialog("Send success ", message, true)
                        dialog.dismiss()
                    } else {
                        notifyDialog("Send Failed", message, false)
                        dialog.dismiss()
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

        val parentLayout: ConstraintLayout = dialog.findViewById(R.id.parentLayout)!!
        val emailLayout: TextInputLayout = dialog.findViewById(R.id.textInputLayout)!!
        setupHideKeyboardOnTouchOutside(listOf(emailLayout), parentLayout)

        dialog.show()
    }

    private fun notifyDialog(titleDialog: String, messageDialog: String, isSuccess: Boolean) {
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.notify_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val title = dialog.findViewById<TextView>(R.id.tvTitle)!!
        val message = dialog.findViewById<TextView>(R.id.tvMessage)!!
        val okButton = dialog.findViewById<Button>(R.id.okButton)!!

        title.text = titleDialog
        message.text = messageDialog
        if (isSuccess) {
            title.setTextColor(ContextCompat.getColor(this, R.color.green))
            message.setTextColor(ContextCompat.getColor(this, R.color.green))
        } else {
            title.setTextColor(ContextCompat.getColor(this, R.color.red))
            message.setTextColor(ContextCompat.getColor(this, R.color.red))
        }


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

        if (validateTextInputLayouts(fieldsToValidate) && isValidEmail(
                email.editText?.text.toString(),
                this
            )
        ) {
            authViewModel.logIn(
                email.editText?.text.toString().trim(),
                password.editText?.text.toString().trim()
            ) { userResponse ->
                when (userResponse) {
                    is Resource.Success -> {
                        //set current user = userResponse from callback
                        authViewModel.currentUser.value = userResponse.data

                        intent<MainActivity>()
                        this.finish()

                    }

                    is Resource.Failure -> {
                        val errorMessage = userResponse.message
                        toast(errorMessage, this)
                    }

                }
            }
        }
    }
}