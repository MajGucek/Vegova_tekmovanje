package com.example.vegova_tekmovanje.network

import com.example.vegova_tekmovanje.data.ElectricityData
import com.example.vegova_tekmovanje.data.TimeData
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    @GET("api")
    suspend fun getTime(@Query("t") hours: Int): List<TimeData>

    @GET("api/kwh")
    suspend fun getKWH(@Query("t") hours: Int): List<ElectricityData>
}
