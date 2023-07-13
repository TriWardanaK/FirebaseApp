package com.example.firebaseapp.view.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseapp.view.home.HomeActivity
import com.example.firebaseapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            loginUser(email, password)
        } else {
            Toast.makeText(
                this@LoginActivity,
                "Email and Password are required",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Login failed: " + task.exception?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}