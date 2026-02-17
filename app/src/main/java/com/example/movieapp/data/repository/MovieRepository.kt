package com.example.movieapp.data.repository

import com.example.movieapp.R
import com.example.movieapp.data.local_db.MovieDao
import com.example.movieapp.data.models.Movie
import com.example.movieapp.data.remote_db.MovieService
import com.example.movieapp.utils.Constants
import com.example.movieapp.utils.Resource
import com.example.movieapp.utils.performFetchingAndSaving
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val movieService: MovieService,
    private val movieDao: MovieDao
) {

    fun getPopularMovies() = performFetchingAndSaving(
        localDbFetch = { movieDao.getAllMovies() },
        remoteDbFetch = {
            val response = movieService.getPopularMovies(Constants.API_KEY)
            if (response.isSuccessful && response.body() != null) {
                Resource.success(response.body()!!.results)
            } else {
                Resource.error("Failed to fetch movies")
            }
        },
        localDbSave = { remoteMovies ->
            remoteMovies.forEach { remoteMovie ->
                val localMovie=movieDao.getMovieSync(remoteMovie.id)
                if (localMovie != null) {
                    val flagMovie = remoteMovie.copy(
                        isFavorite = localMovie.isFavorite,
                        isWatched = localMovie.isWatched,
                        isInWatchList = localMovie.isInWatchList,
                        isManualEntry = localMovie.isManualEntry,
                        rating = if (localMovie.isManualEntry){
                            localMovie.rating
                        }
                        else{
                            remoteMovie.rating
                        }
                    )
                    movieDao.updateMovie(flagMovie)
                } else {
                    movieDao.insertMovie(remoteMovie)
                }
            }}
    )

    fun getFavoriteMovies() = movieDao.getFavoriteMovies()
    fun getWatchedMovies() = movieDao.getWatchedMovies()

    fun getMovie(id: Int) = performFetchingAndSaving(
        localDbFetch = { movieDao.getMovie(id) },
        remoteDbFetch = {
            val localMovie = movieDao.getMovieSync(id)
            if (localMovie?.isManualEntry == true) {
                Resource.success(localMovie)
            } else {
                val response = movieService.getMovieDetails(id, Constants.API_KEY)
                if (response.isSuccessful && response.body() != null) {
                    Resource.success(response.body()!!)
                } else {
                    Resource.error(R.string.failed_to_fetch_movie_details_from_api.toString())
                }
            }
        },
        localDbSave = { remoteMovie ->
            val localMovie=movieDao.getMovieSync(remoteMovie.id)
            if (localMovie != null) {
                val flagMovie = remoteMovie.copy(
                    isFavorite = localMovie.isFavorite,
                    isWatched = localMovie.isWatched,
                    isInWatchList = localMovie.isInWatchList,
                    isManualEntry = localMovie.isManualEntry,
                    rating = if (localMovie.isManualEntry){
                        localMovie.rating
                    }
                    else{
                        remoteMovie.rating
                    }
                )
                movieDao.updateMovie(flagMovie)
            } else {
                movieDao.insertMovie(remoteMovie)
            }
        }
    )

    suspend fun updateMovie(movie: Movie) = withContext(Dispatchers.IO) {
        movieDao.updateMovie(movie)
    }
    suspend fun insertMovie(movie: Movie) = withContext(Dispatchers.IO) {
        movieDao.insertMovie(movie)
    }
    suspend fun searchMovies(query: String) = withContext(Dispatchers.IO) {
        movieService.searchMovies(Constants.API_KEY, query)
    }
    suspend fun deleteMovie(movie: Movie) = withContext(Dispatchers.IO) {
        movieDao.deleteMovie(movie)
    }
}
