package com.example.movieapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentCinemaBinding
import com.example.movieapp.ui.viewmodel.CinemaViewModel
import com.example.movieapp.utils.autoCleared
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CinemaFragment : Fragment(R.layout.fragment_cinema), OnMapReadyCallback {

    private var binding: FragmentCinemaBinding by autoCleared()
    private val viewModel: CinemaViewModel by viewModels()
    private var googleMap: GoogleMap? = null

    // Permission launcher based on lecturer's structure
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startObservingLocation()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCinemaBinding.bind(view)

        // Initialize map
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        checkLocationPermissions()

    }

    private fun checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startObservingLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun startObservingLocation() {
        // One-time observer to focus map on user
        viewModel.locationData.observe(viewLifecycleOwner) { location ->
            location?.let {
                val userLatLng = LatLng(it.latitude, it.longitude)

                googleMap?.let { map ->
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                    map.addMarker(MarkerOptions().position(userLatLng).title("You are here"))

                    // Stop tracking once focused
                    viewModel.locationData.removeObservers(viewLifecycleOwner)
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.uiSettings.isZoomControlsEnabled = true
    }

    // Crucial MapView lifecycle management
    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // MapView must be destroyed with the view
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }
}