package com.example.movieapp.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: Movie)

    @Update
    suspend fun update(movie: Movie)

    @Delete
    suspend fun delete(movie: Movie)

    @Query("SELECT * FROM movies_table WHERE isPublic = 1")
    fun getPublicMovies(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies_table WHERE isPublic = 0")
    fun getUserMovies(): LiveData<List<Movie>>

    // For pre-populating the DB with public movies
    @Query("SELECT * FROM movies_table LIMIT 1")
    suspend fun getAnyMovie(): Movie?
}