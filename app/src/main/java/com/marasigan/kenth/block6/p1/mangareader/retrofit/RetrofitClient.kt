package com.marasigan.kenth.block6.p1.mangareader.retrofit

import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://api.twilio.com/"
    private const val ACCOUNT_SID = "ACee01cbcad5140d92814fac7801648115"
    private const val AUTH_TOKEN = "34f775b6ee5a89615e1c515cec533f83"

    private val okHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
        val request = chain.request().newBuilder()
            .header("Authorization", Credentials.basic(ACCOUNT_SID, AUTH_TOKEN))
            .build()
        chain.proceed(request)
    }.build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}
