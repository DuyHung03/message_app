package com.example.message.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.message.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import javax.inject.Singleton

@Suppress("DEPRECATION")
@Singleton
class AuthRepository(
    private var application: Application,
) {

    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore.apply {
        firestoreSettings = firestoreSettings {
            isPersistenceEnabled = true
        }
    }
    val currentUser: MutableLiveData<FirebaseUser?> = MutableLiveData(auth.currentUser)

    fun signUp(
        email: String,
        password: String,
        callback: (Resource<FirebaseUser?>) -> Unit,
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    addNewUserToDatabase(email, password, task.result.user!!.uid)
                    callback(Resource.Success(null))
                } else {
                    val errorMessage = task.exception?.message ?: "Unknown error"
                    callback(Resource.Failure(errorMessage, task.exception))
                    Log.e("TAG", errorMessage)
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
                    callback(Resource.Success(currentUser.value))
                } else {
                    Toast.makeText(application, task.exception?.message, Toast.LENGTH_LONG)
                        .show()
                }
            }
    }

    private fun addNewUserToDatabase(
        email: String,
        password: String,
        uid: String,
    ) {
        //hashMap User
        val user = hashMapOf(
            "email" to email,
            "password" to password,
            "userId" to uid,
            "displayName" to null,
            "photoURL" to null
        )
        //add user with generate id
        db.collection("users")
            .document(uid)
            .set(user)
            .addOnSuccessListener { _ ->
                Log.d("TAG", "DocumentSnapshot added with ID: $uid")
            }.addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }
    }

    fun logOut() {
        auth.signOut()
        currentUser.postValue(null)
        if (currentUser.value == null) {
            Log.d("TAG", "logOut:")
        }
    }

//    fun verifyEmail(email: String) {
//        if (currentUser.value != null) {
//            currentUser.value!!.sendEmailVerification()
//        }
//    }

    fun sendPasswordResetEmail(
        email: String,
        callback: (Boolean, String) -> Unit,
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "sendPasswordResetEmail: ${task.result}")
                    callback(true, "Password reset email sent to $email successfully")
                } else {
                    val errorMessage = task.exception?.message ?: "Unknown error!"
                    callback(false, errorMessage)
                    Log.d("TAG", "sendPasswordResetEmail: $errorMessage")
                }
            }
    }
}
