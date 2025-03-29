package com.marasigan.kenth.block6.p1.mangareader

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

        database.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val user = userSnapshot.getValue(HelperClass::class.java)
                            if (user != null && user.password == password) {
                                showOtpSelectionDialogue(user)
                            }
                            return
                        }
                        binding.passwordLogin.error = "Invalid Credentials"
                        binding.passwordLogin.requestFocus()
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

    private fun showOtpSelectionDialogue(user: HelperClass){
        val options = arrayOf("Email", "SMS")
        AlertDialog.Builder(this)
            .setTitle("Choose OTP Verification Method")
            .setItems(options){_, which ->
                when(which){
                    0 -> emailAuth(user)
                    1 -> smsAuth(user)
                }
            }
            .setCancelable(true)
            .show()
    }

    private fun emailAuth(user: HelperClass){
        Intent(this@LoginActivity, EmailAuthenticationActivity::class.java).apply {
            putExtra("userId", user.userId)
            putExtra("email", user.email)
            putExtra("username", user.username)
            putExtra("phone", user.phone)
            putExtra("password", user.password)
            startActivity(this)
        }
        finish()
    }

    private fun smsAuth(user: HelperClass){
        Intent(this@LoginActivity, SmsFactorAuthenticationActivity::class.java).apply {
            putExtra("userId", user.userId)
            putExtra("email", user.email)
            putExtra("username", user.username)
            putExtra("phone", user.phone)
            putExtra("password", user.password)
            startActivity(this)
        }
        finish()
    }

}
