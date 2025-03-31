package com.marasigan.kenth.block6.p1.mangareader.helper

import android.util.Log
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object TwilioHelper {
    private val ACCOUNT_SID: String = System.getenv("TWILIO_ACCOUNT_SID") ?: "ACddd5d9ba010b2bcbc2c9c64c47c1e3c8"
    private val AUTH_TOKEN: String = System.getenv("TWILIO_AUTH_TOKEN") ?: "ad5bb64dda354b60143989f37d45a828"
    private val TWILIO_PHONE_NUMBER: String = System.getenv("TWILIO_PHONE_NUMBER") ?: "+12185229850"
    private val VERIFY_SERVICE_SID: String = System.getenv("TWILIO_VERIFY_SERVICE_SID") ?: "VAffb71bdc62bc44183982b7667d09d091"

    private val TWILIO_SMS_URL = "https://api.twilio.com/2010-04-01/Accounts/$ACCOUNT_SID/Messages.json"
    private val VERIFY_URL = "https://verify.twilio.com/v2/Services/$VERIFY_SERVICE_SID"

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    /**
     * Send OTP via SMS using Twilio
     */
    fun sendOTP(phone: String, otp: String, callback: (Boolean) -> Unit) {
        val requestBody = FormBody.Builder()
            .add("To", phone)
            .add("From", TWILIO_PHONE_NUMBER)
            .add("Body", "Your OTP code is: $otp")
            .build()

        val request = Request.Builder()
            .url(TWILIO_SMS_URL)
            .post(requestBody)
            .header("Authorization", Credentials.basic(ACCOUNT_SID, AUTH_TOKEN))
            .build()

        Log.d("TwilioHelper", "Sending SMS OTP to: $phone")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("TwilioHelper", "Failed to send OTP: ${e.message}")
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("TwilioHelper", "Twilio SMS Response: $responseBody")

                callback(response.isSuccessful)
            }
        })
    }

    /**
     * Send OTP via Email using Twilio Verify API
     */
    fun sendEmailOTP(email: String, callback: (Boolean) -> Unit) {
        if (!email.contains("@")) {
            Log.e("TwilioHelper", "Invalid email format: $email")
            callback(false)
            return
        }

        val requestBody = FormBody.Builder()
            .add("To", email)
            .add("Channel", "email")
            .build()

        val request = Request.Builder()
            .url("$VERIFY_URL/Verifications")
            .post(requestBody)
            .header("Authorization", Credentials.basic(ACCOUNT_SID, AUTH_TOKEN))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .build()

        Log.d("TwilioHelper", "Sending Email OTP to: $email")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("TwilioHelper", "Failed to send Email OTP: ${e.message}")
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("TwilioHelper", "Twilio Email Response: $responseBody")

                callback(response.isSuccessful)
            }
        })
    }

    /**
     * Verify OTP (for both SMS and Email)
     */
    fun verifyOTP(identifier: String, otp: String, callback: (Boolean) -> Unit) {
        val requestBody = FormBody.Builder()
            .add("To", identifier)  // Supports both phone and email
            .add("Code", otp)
            .build()

        val request = Request.Builder()
            .url("$VERIFY_URL/VerificationCheck")
            .post(requestBody)
            .header("Authorization", Credentials.basic(ACCOUNT_SID, AUTH_TOKEN))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .build()

        Log.d("TwilioHelper", "Verifying OTP for: $identifier")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("TwilioHelper", "Failed to verify OTP: ${e.message}")
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("TwilioHelper", "Twilio Verification Response: $responseBody")

                if (response.isSuccessful) {
                    try {
                        val jsonObject = JSONObject(responseBody ?: "")
                        val status = jsonObject.optString("status", "")

                        if (status == "approved") {
                            Log.d("TwilioHelper", "OTP verification successful!")
                            callback(true)
                        } else {
                            Log.e("TwilioHelper", "OTP verification failed: status = $status")
                            callback(false)
                        }
                    } catch (e: JSONException) {
                        Log.e("TwilioHelper", "JSON parsing error: ${e.message}")
                        callback(false)
                    }
                } else {
                    Log.e("TwilioHelper", "HTTP error: ${response.code}")
                    callback(false)
                }
            }
        })
    }
    //voice
    fun sendVoiceOTP(phoneNumber: String, callback: (Boolean) -> Unit) {
        val requestBody = FormBody.Builder()
            .add("To", phoneNumber)
            .add("Channel", "call")  // ✅ Use "call" for voice OTP
            .build()

        val request = Request.Builder()
            .url("$VERIFY_URL/Verifications")
            .post(requestBody)
            .header("Authorization", Credentials.basic(ACCOUNT_SID, AUTH_TOKEN))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .build()

        Log.d("TwilioHelper", "Sending Voice OTP to: $phoneNumber")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("TwilioHelper", "Failed to send Voice OTP: ${e.message}")
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("TwilioHelper", "Twilio Voice OTP Response: $responseBody")

                callback(response.isSuccessful)
            }
        })
    }


}