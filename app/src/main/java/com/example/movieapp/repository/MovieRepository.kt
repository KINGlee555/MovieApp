package com.example.movieapp.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.movieapp.data.Movie
import com.example.movieapp.data.MovieDao
import com.example.movieapp.data.MovieDatabase

class MovieRepository(application: Application) {

    // Reference to the DAO
    private var movieDao: MovieDao?

    init {
        // Initialize the database and get the DAO
        val db = MovieDatabase.getDatabase(application)
        movieDao = db.movieDao()
    }

    // Get all movies from the DB as LiveData
    fun getMovies(): LiveData<List<Movie>>? {
        return movieDao?.getAllMovies()
    }

    // Use suspend for background operations with Coroutines
    suspend fun addMovie(movie: Movie) {
        movieDao?.insert(movie)
    }

    suspend fun updateMovie(movie: Movie) {
        movieDao?.update(movie)
    }

    suspend fun deleteMovie(movie: Movie) {
        movieDao?.delete(movie)
    }
}