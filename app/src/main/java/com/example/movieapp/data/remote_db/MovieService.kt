package com.example.movieapp.data.remote_db

import com.example.movieapp.data.models.AllMovies
import com.example.movieapp.data.models.Movie
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {

    // 1. קבלת רשימת סרטים פופולריים לדף הבית
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Response<AllMovies>

    // 2. חיפוש סרט לפי שם
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String = "en-US"
    ): Response<AllMovies>

    // 3. קבלת פרטים מלאים על סרט ספציפי (לפי ה-ID שלו)
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US"
    ): Response<Movie>
}