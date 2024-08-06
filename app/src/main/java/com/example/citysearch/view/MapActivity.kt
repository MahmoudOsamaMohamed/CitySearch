package com.example.citysearch.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.citiesearch.model.pojos.Coordinates
import com.example.citysearch.R
import com.example.citysearch.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var coordinates: Coordinates

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpMap()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        addMarkers(googleMap,coordinates)
    }

    private fun addMarkers(googleMap: GoogleMap,coordinates:Coordinates) {
        googleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(coordinates.lat, coordinates.lon))
        )
        // Move the camera to the marker
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(coordinates.lat, coordinates.lon), 10f))
    }
    private fun setUpMap(){
        coordinates = intent.getSerializableExtra(COORDINATES) as Coordinates
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
}