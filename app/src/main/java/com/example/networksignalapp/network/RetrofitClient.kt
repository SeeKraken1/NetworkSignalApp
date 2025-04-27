package com.example.networksignalapp.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton Retrofit client for API calls
 */
object RetrofitClient {
    // Server URL (default for Android emulator)
    private const val BASE_URL = "http://10.0.2.2:5000/"

    // Configurable timeout values
    private const val CONNECT_TIMEOUT = 15L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 15L

    /**
     * Create OkHttpClient with logging and timeouts
     */
    private val okHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Create Gson converter factory with date serialization
     */
    private val gsonConverterFactory by lazy {
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create()

        GsonConverterFactory.create(gson)
    }

    /**
     * Create Retrofit instance with our configuration
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    /**
     * Create API service interface
     */
    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }

    /**
     * Update the base URL (for different environments)
     */
    fun updateBaseUrl(newBaseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(newBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }
}