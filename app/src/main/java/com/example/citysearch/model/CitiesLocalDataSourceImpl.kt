package com.example.citiesearch.model

import android.content.Context
import android.util.Log
import com.example.citiesearch.model.pojos.City
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.buffer
import okio.source
import javax.inject.Inject

class CitiesLocalDataSourceImpl @Inject constructor(
    private val context: Context
) : CitiesLocalDataSource {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()


    /*
    I decided to read the JSON file in chunks of 500 cities
    and emit it as a Flow<List<City>> to show the progress in the UI
    and don't have to load all cities in memory at once and make user
    wait for whole process to finish to see the cities
    */


    override suspend fun loadCities(): Flow<List<City>> = flow {
        // Open the JSON file as an input stream
        val inputStream = context.assets.open("cities.json")
        val reader = JsonReader.of(inputStream.source().buffer())

        // Begin reading the JSON array
        reader.beginArray()

        // Define chunk size
        val chunkSize = 500
        var currentChunk = mutableListOf<City>()

        // Read each city from the JSON array
        while (reader.hasNext()) {
            val city = moshi.adapter(City::class.java).fromJson(reader)
            if (city != null) {
                currentChunk.add(city)
                // Emit the chunk when it reaches the defined size
                if (currentChunk.size >= chunkSize) {
                    emit(currentChunk.toList())
                    currentChunk.clear()
                }
            }
        }

        // Emit any remaining cities that didn't fill up the last chunk
        if (currentChunk.isNotEmpty()) {
            emit(currentChunk)
        }

        // Close the reader
        reader.endArray()
        inputStream.close()

    }
}
