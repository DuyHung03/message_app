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
                    //check if password changed, update password field in database
                    updateNewPassword(email, password)

                    callback(Resource.Success(currentUser.value))
                } else {
                    Toast.makeText(application, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun updateNewPassword(email: String, password: String) {
        val userRef = db.collection("users").document(email)
        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val doc = task.result
                if (doc != null) {
                    val isPasswordUpdated = doc.getBoolean("isPasswordUpdated") ?: false

                    if (isPasswordUpdated) {
                        //update password
                        userRef.update("password", password)
                        //reset isPasswordUpdated to false
                        userRef.update("isPasswordUpdated", false)
                    }
                }
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
            "photoURL" to null,
            "isPasswordUpdated" to false
        )
        //add user with generate id
        db.collection("users").document(email).set(user)
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
        // Check if the email exists in Firestore
        val userRef = db.collection("users").document(email)
        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Email exists, proceed to send the password reset email
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                //change isPasswordUpdated to true
                                userRef.update("isPasswordUpdated", true)

                                //return callback
                                callback(true, "Password reset email sent to $email successfully")
                            } else {
                                val errorMessage = task.exception?.message ?: "Unknown error!"
                                callback(false, errorMessage)
                                Log.d("TAG", "sendPasswordResetEmail: $errorMessage")
                            }
                        }
                } else {
                    // Email doesn't exist, provide an error message
                    callback(false, "Email $email is not found!.")
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors here.
                callback(false, "Error while checking email existence: ${exception.message}")
            }
    }

}
