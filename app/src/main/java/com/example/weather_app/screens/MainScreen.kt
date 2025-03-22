package com.example.weather_app.screens

import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.text.style.BackgroundColorSpan
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import coil.compose.AsyncImage
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weather_app.R
import com.example.weather_app.ui.theme.Bluegray
import com.google.android.gms.location.LocationServices
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context.LOCATION_SERVICE
import android.location.LocationListener
import android.location.LocationManager
import java.io.IOException

const val API_KEY = "205d318c4b0644489dd133645251203"


@Composable
@Preview(showBackground = true)
fun MainScreen() {



    val state = remember{ mutableStateOf("")}
    val context = LocalContext.current
    val city : String = "Kaliningrad"


    getResultForDay(city, state, context)


    val dataList = state.value.split(", ")



    Image(painter = painterResource(id = R.drawable.sky),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.4f),
        contentScale = ContentScale.Crop)
    Column(modifier = Modifier
        .fillMaxSize()) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Bluegray),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            }


        }
        Box(modifier = Modifier.fillMaxWidth().background(Color.Transparent), contentAlignment = Alignment.Center){
            Text(modifier = Modifier.padding(top = 40.dp),
                text = "$city",
                style = TextStyle(fontSize = 18.sp, color = Color.Blue)



            )

            Text(modifier = Modifier.padding(top = 110.dp),
                text = if (dataList.size > 1) "${dataList[0]}C" else "", // Добавлена проверка размера
                style = TextStyle(fontSize = 50.sp, color = Color.Blue)
            )

            Text(
                modifier = Modifier.padding(top = 170.dp),
                text = if (dataList.size > 1) "${dataList[1]}" else "", // Добавлена проверка размера
                style = TextStyle(fontSize = 18.sp, color = Color.Blue)
            )

        }

        Spacer(modifier = Modifier.weight(0.25f))


        Box(modifier = Modifier.weight(0.4f)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    val xdd: MutableList<WeatherCard> = mutableListOf()
                    val currentTime = LocalTime.now()
                    val formattedTime = currentTime.format(DateTimeFormatter.ofPattern("HH"))

                    val dataList = state.value.split(", ")

                    Log.d("stateeeeee", "${dataList}")
                    for(i in formattedTime.toInt()+2 until dataList.size ){
                        val tempAtMidnight =  dataList[i].split("+")[1]
                        val timee =  dataList[i].split("+")[0]
                        val icon = dataList[i].split("+")[2].replace("]", "")
                        Log.e("temp", "$tempAtMidnight")
                        Log.e("timee", "$timee")
                        Log.e("icon", "$icon")
                        xdd.add(
                            WeatherCard(
                                "",
                                "${timee}",
                                "${tempAtMidnight}С",
                                "http:$icon"
                            )
                        )
                    }
                    if(xdd.size < 9){
                        repeat(9-xdd.size){
                            xdd.add(
                                WeatherCard(
                                    "",
                                    "",
                                    "",
                                    ""
                                )
                            )
                        }
                    }
                        itemsIndexed(xdd) { _, item ->
                            TempCard(item = item)
                    }
                }
            }
        }
    }
}


fun getResultForDay(city : String, state: MutableState<String>, context : Context){
    val url = "http://api.weatherapi.com/v1/forecast.json" + "?key=$API_KEY&" + "q=$city" + "&aqi=no"
    Log.e("DASDASDASDS", "$city")

    var resultList = mutableListOf<String>()


    val queue = Volley.newRequestQueue(context)
    val stringRequest = StringRequest(
        Request.Method.GET,
        url,
        {
            response ->
            Log.d("API_RESPONSE", response)
            val obj = JSONObject(response)

            val nowInfo = obj
                .getJSONObject("current")

            val currentTemp = nowInfo.getString("temp_c")
            val desc = nowInfo.getJSONObject("condition").getString("text")
            Log.d("API_RESPONSE2", desc)

            val hoursArray = obj
                .getJSONObject("forecast")
                .getJSONArray("forecastday")
                .getJSONObject(0)
                .getJSONArray("hour")

            for (i in 0 until hoursArray.length()) {
                val icon = hoursArray.getJSONObject(i).getJSONObject("condition").getString("icon")
                val tempC = hoursArray.getJSONObject(i).getString("temp_c")
                val hourC = hoursArray.getJSONObject(i).getString("time").split(' ')[1]
                Log.e("icon", "$icon")
                resultList.add("$hourC+$tempC+$icon")

            }

            Log.d("state", "$resultList")
            state.value = "$currentTemp, $desc, $resultList"
        },
        {
            error ->
            Log.e("PIZDA", "Error occurred: ${error.message}")
        }
    )
    Log.d("API_REQUEST", "Request is being added to the queue")

    queue.add(stringRequest)
}



