package com.example.vegova_tekmovanje.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://localhost/vticnica/izrocevalec"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY  // Logs the request and response body
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // Add the logging interceptor
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)  // Use the OkHttpClient with the logging interceptor
        .build()

    val apiService: ApiService by lazy {
        // Create a Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL) // Use the correct URL here
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient()) // Add OkHttpClient for logging and requests
            .build()

        retrofit.create(ApiService::class.java) // Create the service
    }
}
