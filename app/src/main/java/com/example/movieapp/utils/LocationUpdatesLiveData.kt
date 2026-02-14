package com.example.movieapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class LocationUpdatesLiveData(context: Context) : LiveData<Location>() {

    private val appContext = context.applicationContext
    private val locationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(appContext)

    // Maintaining the Job and Scope from the lecturer's structure
    private val job = Job()
    private val scope = CoroutineScope(job + Dispatchers.IO)

    // Using the modern Builder to avoid deprecated methods
    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        TimeUnit.SECONDS.toMillis(20)
    ).build()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            p0.lastLocation?.let { location ->
                // Using the scope to post the value like in the original example
                scope.launch {
                    postValue(location)
                }
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onActive() {
        super.onActive()
        startLocationUpdates()
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun startLocationUpdates() {
        // Checking permissions to satisfy Lint without SuppressLint
        if (ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                locationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                Log.e("LocationUpdates", "Security Exception: ${e.message}")
            }
        } else {
            Log.d("LocationUpdates", "Missing location permission")
        }
    }

    override fun onInactive() {
        super.onInactive()
        // Cleaning up job and updates
        job.cancel()
        locationClient.removeLocationUpdates(locationCallback)
    }
}