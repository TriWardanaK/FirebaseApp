package com.example.firebaseapp.view.update

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseapp.databinding.ActivityUpdateBinding
import com.example.firebaseapp.model.remote.response.UserResponse
import com.example.firebaseapp.view.user.UserActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UpdateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storageRef: StorageReference
    private lateinit var galleryLauncher: ActivityResultLauncher<String>

    private val pickImageRequest = "image/*"
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()

        galleryLauncher()
        fetchDataFromFirestore()

        binding.selectImageButton.setOnClickListener {
            openGallery()
        }

        binding.uploadButton.setOnClickListener {
            uploadImage()
        }

        binding.updateButton.setOnClickListener {
            updateData()
        }

        binding.UserButton.setOnClickListener {
            user()
        }
    }

    private fun fetchDataFromFirestore() {
        val collectionRef = firestore.collection("users")
        collectionRef.document(getId().toString())
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(UserResponse::class.java)
                    binding.linkEditText.setText(user?.link)
                    Toast.makeText(
                        this@UpdateActivity,
                        "Document retrieved successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this@UpdateActivity, "Document not found", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this@UpdateActivity,
                    "Failed to retrieve document: $exception",
                    Toast.LENGTH_SHORT
                ).show()
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
                            this@UpdateActivity,
                            "Data uploaded successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                this@UpdateActivity,
                                exception.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
        }
    }

    private fun updateData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val link = hashMapOf(
                "user" to userId,
                "link" to binding.linkEditText.text.toString()
            )

            val collectionRef = firestore.collection("users")
            collectionRef.document(getId().toString())
                .update(link as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(
                        this@UpdateActivity,
                        "Document updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this@UpdateActivity,
                        "Failed to update document: $e",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun user() {
        val intent = Intent(this@UpdateActivity, UserActivity::class.java)
        startActivity(intent)
    }

    private fun getId(): String? {
        return intent.getStringExtra("id")
    }
}