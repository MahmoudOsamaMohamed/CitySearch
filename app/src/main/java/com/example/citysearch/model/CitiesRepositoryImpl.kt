package com.example.citiesearch.model


import com.example.citiesearch.model.pojos.City
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CitiesRepositoryImpl @Inject constructor(
    private val localDataSource: CitiesLocalDataSource
): CitiesRepository {
    override suspend fun loadCities(): Flow<List<City>> = localDataSource.loadCities()
}