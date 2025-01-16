package com.example.goalguru

import androidx.appcompat.app.AppCompatActivity


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    //TODO REMOVE: Mock in-memory user storage
    private val registeredUsers = mutableListOf<Pair<String, String>>() // Pair<email/username, password>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameEmailField: EditText = findViewById(R.id.login_username_email)
        val passwordField: EditText = findViewById(R.id.login_password)
        val signInButton: Button = findViewById(R.id.login_button)
        val registerLink: TextView = findViewById(R.id.register_hint_link)

        // TODO REMOVE: Pre-register a user
        registeredUsers.add(Pair("test@example.com", "password123"))


        signInButton.setOnClickListener {
            val usernameEmail = usernameEmailField.text.toString()
            val password = passwordField.text.toString()

            if (registeredUsers.any { it.first == usernameEmail && it.second == password }) {
                Toast.makeText(this, "Sign In Successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }

        registerLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}