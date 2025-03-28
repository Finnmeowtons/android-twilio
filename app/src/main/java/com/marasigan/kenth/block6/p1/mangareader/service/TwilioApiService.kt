package com.marasigan.kenth.block6.p1.mangareader.service
import com.marasigan.kenth.block6.p1.mangareader.helper.EmailOtpRequest
import com.marasigan.kenth.block6.p1.mangareader.helper.OtpRequest
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

data class SmsRequest(val to: String, val from: String, val body: String)

interface TwilioApiService {
    @POST("/send-otp")
    fun sendOtp(@Body otpRequest: OtpRequest): Call<Void>

    @POST("/send-email-otp")
    fun sendEmailOtp(@Body emailOtpRequest: EmailOtpRequest) : Call<Void>
}
