package com.example.movieapp.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "movies_table")
data class Movie(
    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "image_uri")
    val imageUri: String? // Stores the location of the photo
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0 // Room will auto-increment this ID
}