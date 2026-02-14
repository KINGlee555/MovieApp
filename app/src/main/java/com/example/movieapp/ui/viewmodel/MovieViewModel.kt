package com.example.movieapp.ui.viewmodel

import androidx.lifecycle.*
import com.example.movieapp.data.models.Movie
import com.example.movieapp.repository.MovieRepository
import com.example.movieapp.utils.Resource
import com.example.movieapp.utils.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    // --- Popular Movies List ---
    val allMovies: LiveData<Resource<List<Movie>>> = repository.getPopularMovies()

    // --- Single Movie Details (with switchMap) ---
    private val _movieId = MutableLiveData<Int>()

    val movie: LiveData<Resource<Movie>> = _movieId.switchMap { id ->
        repository.getMovie(id)
    }

    fun setMovieId(id: Int) {
        _movieId.value = id
    }

    // --- Filtered Lists ---
    val favoriteMovies: LiveData<List<Movie>> = repository.getFavoriteMovies()
    val watchedMovies: LiveData<List<Movie>> = repository.getWatchedMovies()

    // --- Search ---
    private val _searchResults = MutableLiveData<Resource<List<Movie>>>()
    val searchResults: LiveData<Resource<List<Movie>>> = _searchResults

    fun searchMovies(query: String) {
        viewModelScope.launch {
            _searchResults.postValue(Resource.loading())
            val response = repository.searchMovies(query)
            if (response.isSuccessful) {
                _searchResults.postValue(Resource.success(response.body()?.results ?: emptyList()))
            } else {
                _searchResults.postValue(Resource.error("Search failed"))
            }
        }
    }
    fun addMovie(movie: Movie) {
        viewModelScope.launch {
            repository.insertMovie(movie)
        }
    }
    // --- Update Functions for btnToggleFavorite & btnToggleWatched ---

    fun toggleFavorite() {
        movie.value?.let { resource ->
            if (resource.status is Success) {
                resource.status.data?.let { currentMovie ->
                    val updatedMovie = currentMovie.copy(isFavorite = !currentMovie.isFavorite)
                    updateMovieStatus(updatedMovie)
                }
            }
        }
    }

    fun toggleWatched() {
        movie.value?.let { resource ->
            if (resource.status is Success) {
                resource.status.data?.let { currentMovie ->
                    val updatedMovie = currentMovie.copy(isWatched = !currentMovie.isWatched)
                    updateMovieStatus(updatedMovie)
                }
            }
        }
    }
    fun toggleWatchList() {
        movie.value?.let { resource ->
            if (resource.status is Success) {
                resource.status.data?.let { currentMovie ->
                    val updatedMovie = currentMovie.copy(isInWatchList = !currentMovie.isInWatchList)
                    updateMovieStatus(updatedMovie)
                }
            }
        }
            }
    fun updateMovieStatus(movie: Movie) {
        viewModelScope.launch {
            repository.updateMovie(movie)
        }
    }
    fun deleteMovie(movie: Movie) {
        viewModelScope.launch {
            repository.deleteMovie(movie)
            }
    }
}
