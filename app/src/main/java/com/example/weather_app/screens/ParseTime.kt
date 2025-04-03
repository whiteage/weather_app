package com.example.weather_app.screens

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import com.google.android.gms.common.api.internal.ApiKey
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create


class ParseTime() {
    fun parse(city : String, state: MutableState<List<WeatherResponse>>, context : Context, apiKey: String){
        Log.d("city", "$city")
        val retrofit2 = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://api.weatherapi.com/")
                .build()

        val apiService = retrofit2.create(RetrofitInterface::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            val parsedInfo = apiService.getInfo(city, apiKey)
            if (parsedInfo.location.name.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    state.value += parsedInfo
                }


            }
        }
}
}
