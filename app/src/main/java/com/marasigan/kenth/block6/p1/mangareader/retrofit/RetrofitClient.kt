package com.marasigan.kenth.block6.p1.mangareader.retrofit

import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://192.168.229.138:3000/"
    private const val ACCOUNT_SID = "ACddd5d9ba010b2bcbc2c9c64c47c1e3c8"
    private const val AUTH_TOKEN = "3f0abab6eb137944d29fe1d26e56db9a"

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
