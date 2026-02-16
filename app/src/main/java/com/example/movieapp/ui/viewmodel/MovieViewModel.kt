package com.example.movieapp.ui.viewmodel

import androidx.lifecycle.*
import com.example.movieapp.data.models.Movie
import com.example.movieapp.repository.MovieRepository
import com.example.movieapp.utils.Resource
import com.example.movieapp.utils.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
/*
acts as a bridge between the UI and the Repository. It holds all the data needed for the screen
 */
@HiltViewModel
class MovieViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    /*
    holds the list of popular movies. The fragment listens to it and updates automatically when the Repository finishes refreshing the data
     */
    val allMovies: LiveData<Resource<List<Movie>>> = repository.getPopularMovies()


    private val _movieId = MutableLiveData<Int>()
    /*
    when the movieId changes, switchMap cancels the previous listening and starts listening to the new movie's LiveData
     */
    val movie: LiveData<Resource<Movie>> = _movieId.switchMap { id ->
        repository.getMovie(id)
    }

    fun setMovieId(id: Int) {
        _movieId.value = id
    }


    val favoriteMovies: LiveData<List<Movie>> = repository.getFavoriteMovies()
    val watchedMovies: LiveData<List<Movie>> = repository.getWatchedMovies()


    private val _searchResults = MutableLiveData<Resource<List<Movie>>>()
    val searchResults: LiveData<Resource<List<Movie>>> = _searchResults
    /*
    runs a safe coroutine. It first updates the UI to the 'loading' state, calls the Repository search function, and finally updates the UI with the results or an error message
     */
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
    /*
The function takes the current state of the movie , creates a copy of it with the inverse of isFavorite, and invokes the update function on the Repository. The Repository will update the database,  and the UI will automatically refresh
     */

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
