package com.example.message.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.message.repository.AuthRepository
import com.example.message.util.Resource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel(
    application: Application,
) : ViewModel() {
    private var authRepository: AuthRepository = AuthRepository(application)
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    val currentUser: MutableLiveData<FirebaseUser?> =
        MutableLiveData(authRepository.currentUser.value)

    fun signUp(
        email: String,
        password: String,
        callback: (Resource<FirebaseUser?>) -> Unit,
    ) {
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.signUp(email, password) { res ->
                _loading.value = false
                callback(res)
            }
        }
    }

    fun logIn(
        email: String,
        password: String,
        callback: (Resource<FirebaseUser?>) -> Unit,
    ) {
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.login(email, password) { res ->
                _loading.value = false
                callback(res)
            }
        }
    }

    fun sendPasswordResetEmail(
        email: String,
        callback: (Boolean, String) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.sendPasswordResetEmail(email) { success, message ->
                callback(success, message)
            }
        }
    }

    fun logOut() {
        viewModelScope.launch {
            (Dispatchers.IO)
            authRepository.logOut()
        }
    }
}