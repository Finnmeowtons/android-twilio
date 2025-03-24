package com.marasigan.kenth.block6.p1.mangareader

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.marasigan.kenth.block6.p1.mangareader.databinding.ActivitySmsFactorAuthenticationBinding
import com.marasigan.kenth.block6.p1.mangareader.helper.OtpRequest
import com.marasigan.kenth.block6.p1.mangareader.retrofit.RetrofitClient
import com.marasigan.kenth.block6.p1.mangareader.service.SmsRequest
import com.marasigan.kenth.block6.p1.mangareader.service.TwilioApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.Handler
import android.os.Looper

class SmsFactorAuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySmsFactorAuthenticationBinding
    private lateinit var database: DatabaseReference


    private lateinit var apiService: TwilioApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sms_factor_authentication)
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

        binding = ActivitySmsFactorAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        apiService = RetrofitClient.createService(TwilioApiService::class.java)

        database = FirebaseDatabase.getInstance().getReference("users")

        val formattedPhone = formatToE164(phone.toString())

        val otp = (1000..9999).random().toString()
        Log.e("MyTag", "$otp, $formattedPhone")

        Handler(Looper.getMainLooper()).postDelayed({
            sendSms(formattedPhone, otp)
        }, 3000)

        binding.tvGoBack.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnSubmit.setOnClickListener{
            val editTextOtp = binding.etOtp.text.toString()
            if (editTextOtp.isEmpty()) {
                Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (editTextOtp == otp) {
                Toast.makeText(this, "OTP Verified", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    fun formatToE164(phoneNumber: String): String {
        // Ensure it starts with '0' and is 11 digits long
        if (phoneNumber.length == 11 && phoneNumber.startsWith("0")) {
            return "+63" + phoneNumber.substring(1)
        } else {
            throw IllegalArgumentException("Invalid phone number format")
        }
    }

    private fun sendSms(formattedPhone: String, otp: String) {
        val otpRequest = OtpRequest(phone = formattedPhone, otp = otp)

        apiService.sendOtp(otpRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@SmsFactorAuthenticationActivity, "OTP Sent Successfully!", Toast.LENGTH_SHORT).show()
                    Log.e("MyTag", "SUCCESS")
                } else {
                    Toast.makeText(this@SmsFactorAuthenticationActivity, "Failed to send OTP: ${response.message()}", Toast.LENGTH_SHORT).show()
                    Log.e("MyTag", "FAILED")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@SmsFactorAuthenticationActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                Log.e("MyTag", "ERROR, ${t.localizedMessage}")
            }
        })
    }

}