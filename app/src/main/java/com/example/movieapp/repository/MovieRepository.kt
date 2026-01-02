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
    fun getPublicMovies(): LiveData<List<Movie>>? {
        return movieDao?.getPublicMovies()
    }
    fun getUserMovies(): LiveData<List<Movie>>? {
        return movieDao?.getUserMovies()
    }

    suspend fun checkAndPrepopulate() {
        val anyMovie = movieDao?.getAnyMovie()

        if (anyMovie == null) {
            val initialList = listOf(
                Movie("Inception", "A dream within a dream", "https://m.media-amazon.com/images/S/pv-target-images/cc72ff2193c0f7a85322aee988d6fe1ae2cd9f8800b6ff6e8462790fe2aacaf3.jpg", 4.8f, true),
                Movie("Interstellar", "Space exploration", "https://assets.vogue.com/photos/5891f503153ededd21da512c/master/pass/holding-interstellar.jpg", 4.9f, true),
                Movie("The Dark Knight", "The Joker in Gotham", "https://i0.wp.com/egreg.io/wp-content/uploads/bat.jpg?fit=640%2C333&ssl=1", 5.0f, true),
                Movie("The Matrix", "Reality is a simulation", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRDU4yn7nfGEGlsDyU3O5cP_9sr42t89JUdKg&s", 4.7f, true),
                Movie("Avatar", "A marine on Pandora", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR_HfU8XK6GBU_ply9iN6s0ifVrwRP15DtYuQ&s", 4.2f, true),
                Movie("Gladiator", "Roman General seeks vengeance", "https://upload.wikimedia.org/wikipedia/en/thumb/f/fb/Gladiator_%282000_film_poster%29.png/250px-Gladiator_%282000_film_poster%29.png", 4.6f, true),
                Movie("Joker", "A comedian turns to crime", "https://upload.wikimedia.org/wikipedia/en/9/90/HeathJoker.png", 4.4f, true)
            )

            initialList.forEach { movie ->
                movieDao?.insert(movie)
            }
        }
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