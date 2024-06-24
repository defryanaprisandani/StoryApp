package com.dicoding.picodiploma.loginwithanimation.view.map

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.retrorfit.Result
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMapsBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val viewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        observeStoryResponse()
    }

    private fun observeStoryResponse() {
        viewModel.getStoriesWithLocation().observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val builder = LatLngBounds.Builder()
                    result.data.listStory.forEach { story ->
                        val latLng = LatLng(story.lat ?: 0.0, story.lon ?: 0.0)
                        mMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(story.name ?: "")
                                .snippet(story.description ?: "")
                        )
                        builder.include(latLng)
                    }
                    val bounds = builder.build()
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                }

                is Result.Loading -> {}
                is Result.Error -> {}
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }
}
