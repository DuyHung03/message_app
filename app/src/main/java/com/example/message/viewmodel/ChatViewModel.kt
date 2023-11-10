package com.example.message.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.message.model.Message
import com.example.message.repository.ChatRepository
import com.example.message.util.Resource
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel(
    application: Application,
) : ViewModel() {
    private var chatRepository: ChatRepository = ChatRepository(application)
    val db = chatRepository.db

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    val currentUser: MutableLiveData<FirebaseUser?> =
        MutableLiveData(chatRepository.currentUser.value)

    val storageReference = chatRepository.storageReference

    fun signUp(
        email: String,
        password: String,
        callback: (Resource<FirebaseUser?>) -> Unit,
    ) {
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.signUp(email, password) { res ->
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
            chatRepository.login(email, password) { user ->
                _loading.value = false
                callback(user)
            }
        }
    }

    fun sendPasswordResetEmail(
        email: String,
        callback: (Boolean, String) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.sendPasswordResetEmail(email) { success, message ->
                callback(success, message)
            }
        }
    }

    fun logOut() {
        chatRepository.logOut()
    }

    fun updateDocument(
        docRef: DocumentReference,
        field: String,
        value: Any,
        onSuccess: OnSuccessListener<Void> = OnSuccessListener {},
        onFailure: OnFailureListener = OnFailureListener {},
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.updateDocument(docRef, field, value, onSuccess, onFailure)
        }
    }

    fun updateUserProfile(displayName: String?, photoURI: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.updateUserProfile(displayName, photoURI)
        }
    }

    fun uploadImageToStorage(
        filePath: Uri,
        callback: (String?, String?) -> Unit,
    ) {
        viewModelScope.launch {
            chatRepository.uploadImageToStorage(filePath) { imgUrl, error ->
                callback(imgUrl, error)
            }
        }
    }

    fun addMessageToDb(message: Message){
        viewModelScope.launch {
            chatRepository.addNewMessageToDatabase(message)
        }
    }

}