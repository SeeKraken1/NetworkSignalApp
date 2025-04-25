package com.example.networksignalapp.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/submit")
    fun submitSignalData(@Body request: SubmitRequest): Call<Void>
}