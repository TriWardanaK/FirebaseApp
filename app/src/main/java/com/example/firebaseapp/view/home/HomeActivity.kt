package com.example.firebaseapp.view.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseapp.view.user.UserActivity
import com.example.firebaseapp.databinding.ActivityHomeBinding
import com.example.firebaseapp.model.remote.response.UserResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storageRef: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding: ActivityHomeBinding
    private lateinit var galleryLauncher: ActivityResultLauncher<String>

    private val pickImageRequest = "image/*"
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()

        galleryLauncher()

        binding.selectImageButton.setOnClickListener {
            openGallery()
        }

        binding.uploadButton.setOnClickListener {
            uploadImage()
        }

        binding.saveButton.setOnClickListener {
            saveData()
        }

        binding.UserButton.setOnClickListener {
            user()
        }
    }

    private fun galleryLauncher() {
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    selectedImageUri = it
                    binding.selectedImageView.setImageURI(it)
                }
            }
    }

    private fun openGallery() {
        galleryLauncher.launch(pickImageRequest)
    }

    private fun uploadImage() {
        if (selectedImageUri != null) {
            val userId = auth.currentUser?.uid
            val imageRef = storageRef.child("$userId/${System.currentTimeMillis()}.jpg")

            imageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        binding.linkEditText.setText(imageUrl)
                        Toast.makeText(
                            this@HomeActivity,
                            "Data uploaded successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                this@HomeActivity,
                                exception.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
        }
    }

    private fun saveData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val link = hashMapOf(
                "user" to userId,
                "link" to binding.linkEditText.text.toString()
            )

            firestore.collection("users")
                .add(link)
                .addOnSuccessListener {
                    Toast.makeText(this@HomeActivity, "Data Saved Successfully", Toast.LENGTH_SHORT)
                        .show()
                    val db = FirebaseFirestore.getInstance()
                    val collectionRef = db.collection("users")

                    collectionRef.get()
                        .addOnSuccessListener { querySnapshot ->
                            val userList = ArrayList<UserResponse>()
                            for (document in querySnapshot) {
                                val user = document.toObject(UserResponse::class.java)
                                user.documentId = document.id
                                userList.add(user)
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                this@HomeActivity,
                                "${exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this@HomeActivity, exception.message, Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private fun user() {
        val intent = Intent(this@HomeActivity, UserActivity::class.java)
        startActivity(intent)
    }
}