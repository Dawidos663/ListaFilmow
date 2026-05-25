package com.example.listafilmow.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey
    val id: String,
    val title: String,
    val year: String = "",
    val genre: String = "",
    val rating: String = "",
    val plot: String = "",
    val posterUrl: String = "",
    val isWatched: Boolean = false
)