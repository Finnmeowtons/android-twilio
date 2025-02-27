package com.marasigan.kenth.block6.p1.mangareader

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.marasigan.kenth.block6.p1.mangareader.databinding.ActivitySignupBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSignup.setOnClickListener {
            registerUser()
        }

        binding.redirecttologin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun registerUser() {
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("users")

        val username = binding.usernameSignup.text.toString().trim()
        val email = binding.emailSignup.text.toString().trim()
        val password = binding.passwordSignup.text.toString().trim()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Generate a unique key for the user
        val userId = reference.push().key ?: return

        val user = HelperClass(userId, username, email, password)

        // Save user data under unique ID instead of username
        reference.child(userId).setValue(user).addOnSuccessListener {
            Toast.makeText(this, "Signup Successful!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Signup Failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

}
