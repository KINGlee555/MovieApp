package com.example.movieapp.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.movieapp.data.models.Movie
import com.example.movieapp.repository.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// AndroidViewModel because we need the application context
class MovieViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MovieRepository(application)

    // Separate LiveData for public and user movies
    val publicMovies: LiveData<List<Movie>>? = repository.getPublicMovies()
    val userMovies: LiveData<List<Movie>>? = repository.getUserMovies()

    private val _chosenMovie = MutableLiveData<Movie>()
    val chosenMovie: LiveData<Movie> get() = _chosenMovie

    // init block to check and pre-populate the DB on startup
    init {
        // Dispatchers.IO is used for database/network operations
        viewModelScope.launch(Dispatchers.IO) {
            repository.checkAndPrepopulate()
        }
    }

    // Function to set the selected movie from the list
    fun setMovie(movie: Movie) {
        _chosenMovie.value = movie
    }

    // This ensures that database operations don't block the UI thread

    fun addMovie(movie: Movie) = viewModelScope.launch(Dispatchers.IO) {
        repository.addMovie(movie)
    }

    fun updateMovie(movie: Movie) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateMovie(movie)
    }

    fun deleteMovie(movie: Movie) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteMovie(movie)
    }
}