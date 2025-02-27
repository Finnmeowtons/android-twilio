package com.marasigan.kenth.block6.p1.mangareader

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.marasigan.kenth.block6.p1.mangareader.databinding.ActivityLoginBinding
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("users")

        binding.buttonLogin.setOnClickListener {
            if (validateUsername() && validatePassword()) {
                checkUser()
            }
        }

        binding.redirecttosignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun validateUsername(): Boolean {
        val username = binding.usernameLogin.text.toString().trim()
        return if (username.isEmpty()) {
            binding.usernameLogin.error = "Username cannot be empty"
            false
        } else {
            binding.usernameLogin.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val password = binding.passwordLogin.text.toString().trim()
        return if (password.isEmpty()) {
            binding.passwordLogin.error = "Password cannot be empty"
            false
        } else {
            binding.passwordLogin.error = null
            true
        }
    }

    private fun checkUser() {
        val username = binding.usernameLogin.text.toString().trim()
        val password = binding.passwordLogin.text.toString().trim()

        database.child(username).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(HelperClass::class.java)
                    if (user != null && user.password == password) {
                        Intent(this@LoginActivity, MainActivity::class.java).apply {
                            putExtra("email", user.email)
                            putExtra("username", user.username)
                            putExtra("password", user.password)
                            startActivity(this)
                        }
                        finish()
                    } else {
                        binding.passwordLogin.error = "Invalid Credentials"
                        binding.passwordLogin.requestFocus()
                    }
                } else {
                    binding.usernameLogin.error = "User does not exist"
                    binding.usernameLogin.requestFocus()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LoginActivity, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
