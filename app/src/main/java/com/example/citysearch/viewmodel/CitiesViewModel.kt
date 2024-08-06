package com.example.citiesearch.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citiesearch.model.CitiesRepository
import com.example.citiesearch.model.pojos.City

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CitiesViewModel @Inject constructor(
    private val repository: CitiesRepository
) : ViewModel() {
    //all cities in the json file
    private val _allCities = MutableStateFlow<List<City>>(emptyList())
    val allCities: StateFlow<List<City>> get() = _allCities
    // the result of the search
    private val _resultCities = MutableStateFlow<List<City>>(emptyList())
    val resultCities: StateFlow<List<City>> get() = _resultCities
    // the progress of the search
    private val _citesloading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val citesloading: StateFlow<Boolean> get() = _citesloading
    // did the search start
    private val _isloadingStarted: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isloadingStarted: StateFlow<Boolean> get() = _isloadingStarted


    init {
        loadCities()
    }
/*
    I collect the emits from the repository and concatenate them
    and sort them by name and country
    I used the built-in function sortedWith because it is more efficient and have only nlog(n) complexity
 */
    private fun loadCities() {
        viewModelScope.launch(Dispatchers.IO){
            repository.loadCities()
                .collect { cities ->

                    val sortedCities = (allCities.value + cities).sortedWith(
                        compareBy<City> { it.name }.thenBy { it.country }
                    )
                    _allCities.value = sortedCities
                   _isloadingStarted.value = true
                }
                _citesloading.value = true
        }
    }

    fun searchCities(query: String) {

        if (query.isEmpty()) {
            // if the query is empty return all cities
            _resultCities.value = allCities.value
            return
        }
       val cityIndex = binarySearchCityByName(allCities.value, query)
        /*
        when the city is found I will move to right and left to add all the cities that have the same prefix
        then sort them
         */
        if (cityIndex != -1) {
            val searchResult = mutableListOf<City>()
            var leftIndex = cityIndex
            var rightIndex = cityIndex+1
            while(leftIndex >= 0 && allCities.value[leftIndex].name.substring(0,query.length).lowercase() == query.lowercase()){
                searchResult.add(allCities.value[leftIndex])
                leftIndex--
            }
            while(rightIndex < allCities.value.size && allCities.value[rightIndex].name.substring(0,query.length).lowercase() == query.lowercase()){
                searchResult.add(allCities.value[rightIndex])
                rightIndex++
            }

            _resultCities.value = searchResult.sortedBy { it.name }

        }
        // if the city is not found return an empty list
        else
            _resultCities.value = emptyList()
    }

    /*
        I wrote binary search because it is more efficient and have only log(n) complexity
     */
    fun binarySearchCityByName(cities: List<City>, query: String): Int {
        var left = 0
        var right = cities.size - 1

        while (left <= right) {
            val mid = left + (right - left) / 2
            val midName = cities[mid].name
            Log.d("where is the wrong", midName.substring(0,query.length))
            when {

                midName.substring(0,query.length).equals(query, ignoreCase = true) -> return mid
                midName.substring(0,query.length).compareTo(query, ignoreCase = true) < 0 -> left = mid + 1
                else -> right = mid - 1

            }
        }

        return -1 // City not found
    }
}