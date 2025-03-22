package com.example.weather_app.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.weather_app.ui.theme.Bluegray


@Composable
fun TempCard(item : WeatherCard) {
    Column(modifier = Modifier.background(color = Bluegray).padding(8.dp)) {
        Text(text = item.time)
        AsyncImage(model = item.photo,
            contentDescription = "image",
            modifier = Modifier.size(32.dp))
        Text(text = item.temperature)
    }
}