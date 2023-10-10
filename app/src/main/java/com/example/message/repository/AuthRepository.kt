package com.example.message.repository

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.message.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthRepository(
    private var application: Application,
) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val currentUser: MutableLiveData<FirebaseUser?> = MutableLiveData(auth.currentUser)

    fun signUp(
        email: String,
        password: String,
        callback: (Resource<FirebaseUser?>) -> Unit,
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(Resource.Success(null))
                } else {
                    val errorMessage = task.exception?.message ?: "Unknown error"
                    callback(Resource.Failure(errorMessage, task.exception))
                }
            }
    }

    fun login(
        email: String,
        password: String,
        callback: (Resource<FirebaseUser?>) -> Unit,
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(Resource.Success(auth.currentUser))
                } else {
                    Toast.makeText(application, task.exception?.message, Toast.LENGTH_LONG)
                        .show()
                }
            }
    }

    fun logOut() {
        auth.signOut()
    }
}
