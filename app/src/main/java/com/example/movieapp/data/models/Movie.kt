package com.example.movieapp.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import com.google.gson.annotations.SerializedName


@Entity(tableName = "movies")
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
    var isWatched: Boolean = false
) : Parcelable {
    fun getFullPosterPath() = "https://image.tmdb.org/t/p/w500$posterPath"
}