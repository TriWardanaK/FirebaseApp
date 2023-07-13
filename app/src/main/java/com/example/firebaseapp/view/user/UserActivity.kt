package com.example.firebaseapp.view.user

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaseapp.databinding.ActivityUserBinding
import com.example.firebaseapp.model.remote.response.UserResponse
import com.example.firebaseapp.view.adapter.UserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private lateinit var recyclerAdapter: UserAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        initRecyclerview()
        fetchDataFromFirestore()
    }

    private fun initRecyclerview() {
        binding.rvUser.layoutManager = LinearLayoutManager(this)
        recyclerAdapter = UserAdapter(this)
        binding.rvUser.adapter = recyclerAdapter
    }

    private fun fetchDataFromFirestore() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            firestore.collection("users")
                .whereEqualTo("user", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val dataList = ArrayList<UserResponse>()

                    for (document in querySnapshot) {
                        val user = document.toObject(UserResponse::class.java)
                        user.documentId = document.id
                        dataList.add(user)
                    }
                    recyclerAdapter.setListUser(dataList)
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this@UserActivity, exception.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun deleteData(documentId: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val collectionRef = firestore.collection("users")
            collectionRef.document(documentId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(
                        this@UserActivity,
                        "Document deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this@UserActivity,
                        "Failed to delete document: $e",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}