package com.example.movieapp.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import com.google.gson.annotations.SerializedName
import com.example.movieapp.utils.Constants.Companion.IMAGE_BASE_URL



@Entity(tableName = "movies")// משמש כרטיס זכרון כך שגם אם נסגור את האפל המידע לא יאבד
@Parcelize
data class Movie(
    @PrimaryKey
    val id: Int,
    val title: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("vote_average")
    val rating: Double,
    var isFavorite: Boolean = false,
    var isWatched: Boolean = false,
    var isManualEntry : Boolean = false,
    val isInWatchList: Boolean = false, //
) : Parcelable {
    fun getFullPosterPath(): String? {//פונק שצמיגה את התמונה
        if (posterPath.isNullOrBlank()) return null
        if (posterPath.startsWith("content://") || posterPath.startsWith("http")) {
            return posterPath
        }
        return IMAGE_BASE_URL + posterPath
    }}
/*
הקובץ הזה הוא Data Class. ב-Kotlin, זהו סוג
מיוחד של מחלקה שכל תפקידה הוא להחזיק
נתונים. כאן אנחנו מגדירים איך "סרט" נראה בתוך האפליקציה שלנו –
 אילו תכונות יש לו (שם, תמונה, דירוג וכו').
 getFullPosterPath- פונקציה שמצגיגה את התמונה של הסרט
 */