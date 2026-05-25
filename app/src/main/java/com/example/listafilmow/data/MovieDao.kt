package com.example.listafilmow.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie)

    @Delete
    suspend fun deleteMovie(movie: Movie)

    @Query("SELECT * FROM movies WHERE isWatched = 0")
    fun getWatchlist(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE isWatched = 1")
    fun getWatched(): Flow<List<Movie>>
}