package com.example.movieapp.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.movieapp.data.Movie
import com.example.movieapp.repository.MovieRepository
import kotlinx.coroutines.launch

// We use AndroidViewModel because we need the application context
class MovieViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MovieRepository(application)

    // Livedata that will hold the list of all movies from the DB
    val allMovies: LiveData<List<Movie>>? = repository.getMovies()

    // Shared LiveData to hold the current movie selected for viewing or editing
    private val _chosenMovie = MutableLiveData<Movie>()
    val chosenMovie: LiveData<Movie> get() = _chosenMovie

    // Function to set the selected movie from the list
    fun setMovie(movie: Movie) {
        _chosenMovie.value = movie
    }

    // This ensures that database operations don't block the UI thread

    fun addMovie(movie: Movie) = viewModelScope.launch {
        repository.addMovie(movie)
    }

    fun updateMovie(movie: Movie) = viewModelScope.launch {
        repository.updateMovie(movie)
    }

    fun deleteMovie(movie: Movie) = viewModelScope.launch {
        repository.deleteMovie(movie)
    }
}