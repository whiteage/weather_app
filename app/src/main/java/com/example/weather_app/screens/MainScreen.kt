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
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context.LOCATION_SERVICE
import android.location.Address
import android.location.LocationListener
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.getSystemService
import java.io.IOException

const val API_KEY = "205d318c4b0644489dd133645251203"
var city22 = ""
@Composable
@Preview(showBackground = true)
fun MainScreen() {

    var parsedInfo = ParseTime()
    val state = remember {
        mutableStateOf(
            WeatherResponse(
                Location("", "", "", 0.0, 0.0, "", 0, ""),
                Current(
                    0,
                    "",
                    0.0,
                    0.0,
                    0,
                    Condition("", "", 0),
                    0.0,
                    0.0,
                    0,
                    "",
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0,
                    0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0
                ),
                Forecast(listOf())
            )
        )
    }
    val context = LocalContext.current
    var temp = emptyList<Double>()
    var src = emptyList<String>()
    RequestLocationPermission(context)

    var forecastday = emptyList<String>()
    var currentTemp = 0.0
    var currentWeath = ""




    myCoord(context)
    parsedInfo.parse(city22, state, context, API_KEY)
    if (state.value.forecast.forecastday.size != 0) {
        forecastday = state.value.forecast.forecastday[0].hour.map { it.time }
        temp = state.value.forecast.forecastday[0].hour.map { it.temp_c}
        src = state.value.forecast.forecastday[0].hour.map { it.condition.icon   }


        currentTemp = state.value.current.temp_c
        currentWeath = state.value.current.condition.text
        Log.d("currentWeath", "${state.value.forecast}")

    }




    Image(
        painter = painterResource(id = R.drawable.sky),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.4f),
        contentScale = ContentScale.Crop
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.padding(top = 40.dp),
                text = "$city22",
                style = TextStyle(fontSize = 18.sp, color = Color.Blue)


            )

            Text(
                modifier = Modifier.padding(top = 110.dp),
                text = "$currentTemp С",
                style = TextStyle(fontSize = 50.sp, color = Color.Blue)
            )

            Text(
                modifier = Modifier.padding(top = 170.dp),
                text = "$currentWeath", // Добавлена проверка размера
                style = TextStyle(fontSize = 18.sp, color = Color.Blue)
            )

        }

        Spacer(modifier = Modifier.weight(0.25f))


        Box(modifier = Modifier.weight(0.2f)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    val xdd: MutableList<WeatherCard> = mutableListOf()
                    val currentTime = LocalTime.now()
                    val formattedTime = currentTime.format(DateTimeFormatter.ofPattern("HH"))
                    if(forecastday.size > 0) {
                        for (i in formattedTime.toInt() until 24) {
                            xdd.add(
                                WeatherCard(
                                    "",
                                    "${forecastday[i].split(" ")[1]}",
                                    "${temp[i]}С",
                                    "http:${src[i]}"
                                )
                            )
                        }
                        if (xdd.size < 7){
                            repeat(7-xdd.size){
                            xdd.add(WeatherCard(
                                "",
                                "DASD",
                                "ASDSA",
                                "SADSA"
                            ))}
                        }
                        itemsIndexed(xdd) { _, item ->
                            TempCard(item = item)
                        }
                    }
                }
            }
        }


    }
}

fun myCoord(context: Context) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val provider = LocationManager.GPS_PROVIDER

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        val loc = locationManager.getLastKnownLocation(provider)
        loc?.let { location ->

            val longtitude = location.longitude
            val latitude = location.latitude
            Log.d("Location", "Lat: $latitude, Lon: $longtitude")
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longtitude, 1)
            if (!addresses.isNullOrEmpty()) {
                city22 = addresses[0].locality
                Log.d("Location", "City: $city22")
            }

        } ?: run {
            // Если loc равно null
            Log.d("Location", "LocationZZZZ")
        }


    } else {
        Log.d("Location", "Location not available")
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            100
        )
    }
}
@Composable
fun RequestLocationPermission(context: Context) {

    val activity = context as? Activity

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("Permission", "Доступ к локации разрешён")
        } else {
            Log.d("Permission", "Доступ к локации запрещён")
        }
    }

    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            Log.d("Permission", "Разрешение уже есть")
        }
    }
}