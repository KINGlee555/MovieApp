package com.example.movieapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.movieapp.utils.LocationUpdatesLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CinemaViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    // Initializing the LiveData that handles location updates
    // We pass the application context to avoid memory leaks
    val locationData = LocationUpdatesLiveData(application)
}