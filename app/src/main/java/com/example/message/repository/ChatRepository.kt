package com.example.message.repository

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.message.model.Message
import com.example.message.util.Resource
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Singleton

@Suppress("DEPRECATION")
@Singleton
class ChatRepository(
    private var application: Application,
) {

    private val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore.apply {
        firestoreSettings = firestoreSettings {
            isPersistenceEnabled = true
        }
    }

    private val storage = FirebaseStorage.getInstance()

    val storageReference = storage.reference

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
                    val userRef = db.collection("users").document(email)

                    userRef.get().addOnCompleteListener { doc ->
                        if (doc.isSuccessful) {
                            if (doc.result != null) {
                                val isPasswordUpdated =
                                    doc.result.getBoolean("isPasswordUpdated") ?: false

                                if (isPasswordUpdated) {

                                    //update Password
                                    updateDocument(userRef, "password", password, {
                                        //update isPasswordUpdate to false
                                        updateDocument(userRef, "isPasswordUpdated", true)
                                    },
                                        { e ->
                                            Log.w("TAG", "Error updating document", e)
                                        })
                                }
                            }
                        }
                    }
                    callback(Resource.Success(currentUser.value))
                } else {
                    Toast.makeText(application, task.exception?.message, Toast.LENGTH_LONG).show()
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
            "isPasswordUpdated" to false,
            "isNewUser" to true
        )
        //add user with generate id
        db.collection("users").document(email).set(user)
            .addOnSuccessListener { _ ->
                Log.d("TAG", "DocumentSnapshot added with ID: $uid")
            }.addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }
    }

     fun addNewMessageToDatabase(message: Message) {
        db.collection("messages").document(message.messageId).set(message)
            .addOnSuccessListener { _ ->
                Log.d("TAG", "addNewMessageToDatabase: ${message.messageId + message.message}")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding message", e)
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
                                updateDocument(userRef,
                                    "isPasswordUpdated",
                                    true,
                                    {
                                        Log.d("TAG", "DocumentSnapshot successfully updated!")
                                    },
                                    { e ->
                                        Log.w("TAG", "Error updating document", e)
                                    })

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

    fun updateDocument(
        docRef: DocumentReference,
        field: String,
        value: Any,
        onSuccess: OnSuccessListener<Void> = OnSuccessListener {},
        onFailure: OnFailureListener = OnFailureListener {},
    ) {
        val updateData = hashMapOf<String, Any>()
        updateData[field] = value

        docRef.update(updateData)
            .addOnSuccessListener(onSuccess)
            .addOnFailureListener(onFailure)
    }

    fun updateUserProfile(name: String?, photoURI: String?) {
        val currentPhotoUri = auth.currentUser?.photoUrl.toString()
        val profileUpdates = userProfileChangeRequest {
            displayName = name
            photoUri = Uri.parse(photoURI ?: currentPhotoUri)
        }

        currentUser.value!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "User profile updated.")
                }
            }
    }

    fun uploadImageToStorage(
        filePath: Uri,
        callback: (String?, String?) -> Unit,
    ) {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "image_$timeStamp.jpg"
        val imageReference = storageReference.child("images/$fileName")

        imageReference.putFile(filePath)
            .addOnSuccessListener { snapshot ->
                // Get the download URL from the snapshot
                snapshot.storage.downloadUrl
                    .addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        Log.d("TAG", "uploadImageToStorage: $imageUrl")
                        callback(imageUrl, null)
                    }
                    .addOnFailureListener { e ->
                        callback(null, e.message.toString())
                    }
            }
            .addOnFailureListener { e ->
                callback(null, e.message.toString())
            }
    }
}
