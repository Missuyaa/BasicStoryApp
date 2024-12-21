package com.dicoding.storyapp.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.datastore.DataStoreManager
import com.dicoding.storyapp.data.viewmodel.StoryViewModel
import com.dicoding.storyapp.data.viewmodel.StoryViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var storyViewModel: StoryViewModel
    private val markers = mutableListOf<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Setup ViewModel
        setupViewModel()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupViewModel() {
        val dataStoreManager = DataStoreManager(applicationContext)
        val factory = StoryViewModelFactory(dataStoreManager, applicationContext)
        storyViewModel = ViewModelProvider(this, factory)[StoryViewModel::class.java]
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        loadStoryMarkers()
    }

    private fun loadStoryMarkers() {
        lifecycleScope.launch {
            storyViewModel.getStoriesWithLocation().observe(this@MapsActivity) { stories ->
                Log.d("MapsActivity", "Jumlah cerita dengan lokasi: ${stories.size}")

                markers.forEach { it.remove() }
                markers.clear()

                var firstMarkerAdded = false
                var markerAdded = false // Flag untuk cek apakah ada marker yang ditambahkan

                stories.forEachIndexed { index, story ->
                    val latitude = story.lat
                    val longitude = story.lon

                    if (latitude != null && longitude != null &&
                        latitude in -90.0..90.0 && longitude in -180.0..180.0
                    ) {
                        val adjustedLat = latitude + (index * 0.0001) // Tambahkan offset kecil
                        val adjustedLon = longitude + (index * 0.0001)
                        val position = LatLng(adjustedLat, adjustedLon)

                        val marker = mMap.addMarker(
                            MarkerOptions()
                                .position(position)
                                .title(story.name)
                                .snippet(story.description)
                        )
                        marker?.let { markers.add(it) }
                        markerAdded = true

                        Log.d(
                            "MapsActivity",
                            "Menambahkan marker untuk: ${story.name} di Lat: $adjustedLat, Lon: $adjustedLon"
                        )

                        if (!firstMarkerAdded) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 10f))
                            firstMarkerAdded = true
                        }
                    } else {
                        Log.d(
                            "MapsActivity",
                            "Story ${story.name} memiliki lokasi tidak valid atau tidak ada."
                        )
                    }
                }

                if (markers.isEmpty()) {
                    Toast.makeText(this@MapsActivity, "Tidak ada cerita dengan lokasi.", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }
}