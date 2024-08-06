package com.example.citiesearch.model


import com.example.citiesearch.model.pojos.City
import kotlinx.coroutines.flow.Flow

interface CitiesLocalDataSource {
    suspend fun loadCities(): Flow<List<City>>
}