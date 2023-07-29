package com.example.chatyapp.loginRegisterActivites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.chatyapp.R
import com.example.chatyapp.chatActivites.MessagesActivity
import com.example.chatyapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    companion object {
        const val tag = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        checkIfUserLoggedIn()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.apply {
            loginButton.setOnClickListener {
                performLogin()
            }
            goToRegisterTextView.setOnClickListener {
                goToRegister()
            }
        }

    }

    private fun checkIfUserLoggedIn() {
        val user = FirebaseAuth.getInstance().uid
        if (user != null) {
            val intent = Intent(this@LoginActivity, MessagesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

    private fun goToRegister() {
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun performLogin() {
        val email = binding.usernameLoginEditText.text.toString()
        val password = binding.passwordLoginEditText.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                this@LoginActivity,
                "Please enter the username or password",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "Logged in successfully", Toast.LENGTH_SHORT)
                        .show()
                    Log.i(tag, "User logged in Successfully")
                    val intent = Intent(this@LoginActivity, MessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                } else {
                    return@addOnCompleteListener
                }
            }.addOnFailureListener {
                Toast.makeText(
                    this@LoginActivity,
                    "Failed to login ${it.message}",
                    Toast.LENGTH_SHORT
                )
                    .show()
                Log.i(tag, "Failed to login ${it.message}")
            }
    }
}