package com.example.goalguru

import UserViewModel
import androidx.appcompat.app.AppCompatActivity


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameEmailField: EditText = findViewById(R.id.login_username_email)
        val passwordField: EditText = findViewById(R.id.login_password)
        val signInButton: Button = findViewById(R.id.login_button)
        val registerLink: TextView = findViewById(R.id.register_hint_link)

        signInButton.setOnClickListener {
            val usernameEmail = usernameEmailField.text.toString()
            val password = passwordField.text.toString()

            userViewModel.loginUser(usernameEmail, password)
        }

        registerLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        userViewModel.user.observe(this) { user ->
            updateUI(user)
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            Toast.makeText(this, "Sign In Successful", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }
}