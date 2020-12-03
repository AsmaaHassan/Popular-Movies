package com.example.movielist.data.internal.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_table")
data class MovieEntity(
    @PrimaryKey() val id: Int?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "imagePath") val imagePath: String?,
    @ColumnInfo(name = "rating") val rating: Double?,
    @ColumnInfo(name = "overview") val overview: String?,
    @ColumnInfo(name = "is_firstPage") val is_firstPage: Int?,
    @ColumnInfo(name = "is_favourite") val is_favourite: Int?
)