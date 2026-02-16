package com.example.movieapp.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import com.google.gson.annotations.SerializedName
import com.example.movieapp.utils.Constants.Companion.IMAGE_BASE_URL


/*
 Defines this class as a database  that will be stored in a table named movies

 */
@Entity(tableName = "movies")
/*
Allows this object to be safely sent between fragments when moving between screens
 */
@Parcelize
data class Movie(
    /*
Defines the 'id' field as the unique primary key for each movie in the database
     */
    @PrimaryKey
    val id: Int,
    val title: String,
    /*
    Maps the 'poster_path' field from the JSON response to the 'posterPath' variable in the code
     */
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("vote_average")
    val rating: Double,
    /*
A locally managed flag that reflects a user action and is not received from the network
     */
    var isFavorite: Boolean = false,
    var isWatched: Boolean = false,
    var isManualEntry : Boolean = false,
    val isInWatchList: Boolean = false,
) : Parcelable {
    /*
Converts a partial image path into a complete URL
     */
    fun getFullPosterPath(): String? {
        if (posterPath.isNullOrBlank()) return null
        if (posterPath.startsWith("content://") || posterPath.startsWith("http")) {
            return posterPath
        }
        return IMAGE_BASE_URL + posterPath
    }}