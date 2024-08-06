package com.example.citiesearch.model.pojos

import java.io.Serializable
// I made it Serializable to be able to pass it in an intent to the MapActivity
data class Coordinates(
    val lon: Double,
    val lat: Double
):Serializable