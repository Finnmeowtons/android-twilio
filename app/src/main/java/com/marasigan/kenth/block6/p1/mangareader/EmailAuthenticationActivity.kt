package com.marasigan.kenth.block6.p1.mangareader

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.marasigan.kenth.block6.p1.mangareader.databinding.ActivityEmailAuthenticationBinding
import com.marasigan.kenth.block6.p1.mangareader.helper.EmailOtpRequest
import com.marasigan.kenth.block6.p1.mangareader.retrofit.RetrofitClient
import com.marasigan.kenth.block6.p1.mangareader.service.TwilioApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.Handler
import android.os.Looper

class EmailAuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmailAuthenticationBinding
    private lateinit var database: DatabaseReference

    private lateinit var apiService: TwilioApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_email_authentication)
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

        binding = ActivityEmailAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        apiService = RetrofitClient.createService(TwilioApiService::class.java)

        database = FirebaseDatabase.getInstance().getReference("users")

        val otp = (1000..9999).random().toString()
        Log.e("Mytag", "$otp, $email" )

//        Handler(Looper.getMainLooper()).postDelayed({
//            send
//        }
    }

    private fun sendEmail(userEmail: String, otp: String){
        val emailOtpRequest = EmailOtpRequest(userEmail = userEmail, otp = otp)

        apiService.sendEmailOtp(emailOtpRequest).enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>){
                if (response.isSuccessful){
                    Toast.makeText(this@EmailAuthenticationActivity, "OTP Sent Successfully!", Toast.LENGTH_SHORT).show()
                    Log.e("MyTag", "SUCCESS")
                }else {
                    Toast.makeText(
                        this@EmailAuthenticationActivity,
                        "Failed to send OTP: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("MyTag", "FAILED")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@EmailAuthenticationActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                Log.e("MyTag", "Error ${t.localizedMessage}")
            }

        })
    }
}