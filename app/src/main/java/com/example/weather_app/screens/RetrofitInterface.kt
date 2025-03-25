package com.example.weather_app.screens

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitInterface {
    @GET("v1/forecast.json")
    suspend fun getInfo(
        @Query("q") city: String,
        @Query("key")apiKey: String,
        @Query("aqi")aqi : String = "no"


    ) : WeatherResponse

}