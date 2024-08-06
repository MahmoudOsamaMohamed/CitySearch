package com.example.citiesearch.model



import com.example.citiesearch.model.pojos.City
import kotlinx.coroutines.flow.Flow

interface CitiesRepository {
    suspend fun loadCities(): Flow<List<City>>
}