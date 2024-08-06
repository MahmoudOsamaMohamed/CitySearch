package com.example.citysearch.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.citiesearch.viewmodel.CitiesViewModel
import com.example.citysearch.databinding.ActivityMainBinding

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val COORDINATES = "coordinates"
const val IS_TOAST_SHOWN = "isToastShown"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var isSearching = false
    private lateinit var binding: ActivityMainBinding
    private val viewModel: CitiesViewModel by viewModels()
    private lateinit var adapter: CitiesAdapter
    private var progress = 0
    private var isToastShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isToastShown = savedInstanceState?.getBoolean(IS_TOAST_SHOWN) ?: false
        setupAdapter()
        observeCitiesLoading()
        observeStartingOfTheLoading()
        observeFetchingAllCities()
        observeSearchingResult()
        addOnCityClickListener()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(IS_TOAST_SHOWN, isToastShown)
    }

    private fun setupAdapter() {
        adapter = CitiesAdapter { coordinates ->
            // when the user click on the city in the list i will navigate to the MapActivity at the given coordinates
            binding.progressBar.visibility = android.view.View.VISIBLE
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra(COORDINATES, coordinates)
            startActivity(intent)
            binding.progressBar.visibility = android.view.View.GONE
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager= LinearLayoutManager(this)
    }

    private fun observeCitiesLoading() {
        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.citesloading.collect { loading ->
                    // it will be true when the cities finish loading
                    if (loading) {
                        binding.progressTv.visibility = android.view.View.GONE
                        binding.fetchingProgress.visibility = android.view.View.GONE
                        if(!isToastShown) {
                            Toast.makeText(
                                this@MainActivity,
                                "fetching data has finsihed",
                                Toast.LENGTH_SHORT
                            ).show()
                            isToastShown = true
                        }
                    }
                }

            }
        }
    }

    private fun observeStartingOfTheLoading(){
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.isloadingStarted.collect { loading ->
                // to hide loading bar when the cities start to apear in the screen
                if (loading)
                    binding.progressBar.visibility = android.view.View.GONE
            }
        }
    }

    private fun observeFetchingAllCities(){
        lifecycleScope.launch(Dispatchers.IO) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allCities.collect { cities ->
                    // each time the cities are updated i will update the recycler view and fetch progress and progress text
                    withContext(Dispatchers.Main) {
                        if (!isSearching){

                            adapter.submitList(cities)
                            binding.fetchingProgress.progress = progress++
                            binding.progressTv.text = "loading... ${progress*.5} k"
                        }
                    }
                }
            }
        }
    }

    private fun observeSearchingResult(){
        lifecycleScope.launch(Dispatchers.IO) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.resultCities.collect { cities ->
                    // when the user search for a city i will update the recycler view to show the search result
                    withContext(Dispatchers.Main){
                        adapter.submitList(cities)
                    }
                }
            }
        }
    }

    private fun addOnCityClickListener(){
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            // i will use this to observe the user input in real time
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchCities(s.toString())
                // we will use this to check if the user is searching or not
                if(s.toString().isNullOrEmpty())
                    isSearching = false
                else
                    isSearching = true
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
