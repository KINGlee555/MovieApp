package com.example.movieapp.data.local_db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.movieapp.data.models.Movie

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies")
    fun getAllMovies(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies WHERE id = :id")
    fun getMovie(id: Int): LiveData<Movie>
    @Query("SELECT * FROM movies WHERE isFavorite = 1")
    fun getFavoriteMovies(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies WHERE isWatched = 1")
    fun getWatchedMovies(): LiveData<List<Movie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<Movie>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie)

    @Update
    suspend fun updateMovie(movie: Movie)


    @Query("DELETE FROM movies WHERE isFavorite = 0 AND isWatched = 0")
    suspend fun clearCachedMovies()
}