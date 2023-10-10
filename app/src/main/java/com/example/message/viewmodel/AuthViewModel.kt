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
    private var authRepository: AuthRepository
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    init {
        authRepository = AuthRepository(application)
    }

    val currentUser: MutableLiveData<FirebaseUser?> =
        MutableLiveData(authRepository.auth.currentUser)

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

    fun logOut() {
        authRepository.logOut()
        currentUser.postValue(null)
    }
}