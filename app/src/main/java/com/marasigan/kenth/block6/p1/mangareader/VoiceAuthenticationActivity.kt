package com.marasigan.kenth.block6.p1.mangareader

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.marasigan.kenth.block6.p1.mangareader.databinding.ActivityVoiceAuthenticationBinding
import com.marasigan.kenth.block6.p1.mangareader.helper.TwilioHelper
import com.marasigan.kenth.block6.p1.mangareader.retrofit.RetrofitClient
import com.marasigan.kenth.block6.p1.mangareader.service.TwilioApiService

class VoiceAuthenticationActivity : AppCompatActivity() {

    private lateinit var binding : ActivityVoiceAuthenticationBinding
    private lateinit var database: DatabaseReference

    private lateinit var apiService: TwilioApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_voice_authentication)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userId = intent.getStringExtra("userId")
        val email = intent.getStringExtra("email")
        val username = intent.getStringExtra("username")
        val phone = intent.getStringExtra("phone")
        val password = intent.getStringExtra("password")

        binding = ActivityVoiceAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        apiService = RetrofitClient.createService(TwilioApiService::class.java)
        database = FirebaseDatabase.getInstance().getReference("users")

        val formattedPhone = formatToE164(phone.toString())

        Handler(Looper.getMainLooper()).postDelayed({sendVoice(formattedPhone)}, 3000)

        binding.btnSubmit.setOnClickListener{
            val editTextOtp = binding.etOtp.text.toString()
            if (editTextOtp.isEmpty()) {
                Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            TwilioHelper.verifyOTP(formattedPhone, editTextOtp){success->runOnUiThread{
                if (success){
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                }else{
                    Toast.makeText(this, "Incorrect OTP", Toast.LENGTH_SHORT).show()
                }
            }}
        }

        binding.tvGoBack.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun formatToE164(phoneNumber: String): String {
        // Ensure it starts with '0' and is 11 digits long
        if (phoneNumber.length == 11 && phoneNumber.startsWith("0")) {
            return "+63" + phoneNumber.substring(1)
        } else {
            throw IllegalArgumentException("Invalid phone number format")
        }
    }

    private fun sendVoice(phoneNumber: String){
        TwilioHelper.sendVoiceOTP(phoneNumber) {success -> runOnUiThread{
            if (success){
                Toast.makeText(this, "Voice OTP Sent", Toast.LENGTH_SHORT).show()
                Log.d("OTPVerificationActivity", "Voice OTP successfully sent to $phoneNumber")
            }else{
                Toast.makeText(this, "Voice OTP Failed!", Toast.LENGTH_SHORT).show()
                Log.e("OTPVerificationActivity", "Failed to send Voice OTP")
            }
        }}
    }
}