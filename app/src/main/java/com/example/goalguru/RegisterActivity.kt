package com.example.goalguru

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    //TODO REMOVE: Mock in-memory user storage
    private val registeredUsers = mutableListOf<Pair<String, String>>() // Pair<email/username, password>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val usernameField: EditText = findViewById(R.id.register_name)
        val emailField: EditText = findViewById(R.id.register_email)
        val passwordField: EditText = findViewById(R.id.register_password)
        val repeatPasswordField: EditText = findViewById(R.id.register_repeat_password)
        val registerButton: Button = findViewById(R.id.register_button)
        val signInLink: TextView = findViewById(R.id.login_hint_link)

        registerButton.setOnClickListener {
            val username = usernameField.text.toString()
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            val repeatPassword = repeatPasswordField.text.toString()

            if (username.isBlank() || email.isBlank() || password.isBlank() || repeatPassword.isBlank()) {
                Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != repeatPassword) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (registeredUsers.any { it.first == email }) {
                Toast.makeText(this, "Email already registered.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //TODO UPDATE: Save the new user
            registeredUsers.add(Pair(email, password))
            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()

            // return to sign-in screen
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        signInLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}