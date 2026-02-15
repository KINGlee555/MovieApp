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
}/*

הקובץ הזה הוא ה"תפריט" של האפליקציה מול האינטרנט
אנחנו משתמשות בספריית  Retrofit שהיא בעצם הספרייה שהופכת את הAPI של השרת לממשק שממתאים לקוטלין ומטפלת בלוגיקה ובהמרת הנתונים
אנחנו מצהירות על הפעולות שאנחנו רוצות לעשות (כמו לקבל סרטים פופולריים או לחפש סרט)
ו-Retrofit מבצעת את הפנייה לאינטרנט ומביאה את הנתונים
הקובץ מגדיר אלו בקשות אנחנו יכולים לשלוח ומה אנו מצפים לקבל בחזרה

1. @GET - אומר לאפליקציה "תביא לי נתונים" מכתובת מסוימת בשרת.
2. suspend - אומר שהפעולה הזו תקרה "בצד" (ברקע) כדי שהאפליקציה לא תיתקע בזמן שהיא מחכה לאינטרנט
3. @Query -מה שמאפשר להוסיף פרמטרים וסינונים לכתובת הURL ובאיזו שפה להציג את הסרטים
*/