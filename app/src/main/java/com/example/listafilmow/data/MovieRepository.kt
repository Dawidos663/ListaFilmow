package com.example.listafilmow.data

import com.example.listafilmow.network.RetrofitInstance
import com.example.listafilmow.network.toMovie
import kotlinx.coroutines.flow.Flow

class MovieRepository( // Klasa pośrednicząca między ViewModelem bazą danych i API
    private val dao: MovieDao // DAO używane do operacji na lokalnej bazie danych
) {
    val watchlist: Flow<List<Movie>> = dao.getWatchlist() // Pobiera filmy do obejrzenia jako strumień danych
    val watched: Flow<List<Movie>> = dao.getWatched() // Pobiera filmy obejrzane jako strumień danych

    suspend fun searchMovie(title: String): Movie? { // Wyszukuje film w API po tytule
        val response = RetrofitInstance.api.searchMovie(title) // Wysyła zapytanie do OMDb API

        return if (response.response == "True") { // Sprawdza czy API znalazło film
            response.toMovie() // Zamienia odpowiedź API na obiekt Movie
        } else { // Wykonuje się gdy API nie znalazło filmu
            null // Zwraca brak filmu
        }
    }

    suspend fun addToWatchlist(movie: Movie) { // Dodaje film do listy do obejrzenia
        dao.insertMovie(movie.copy(isWatched = false)) // Zapisuje film jako nieobejrzany
    }

    suspend fun markAsWatched(movie: Movie) { // Oznacza film jako obejrzany
        dao.insertMovie(movie.copy(isWatched = true)) // Zapisuje film jako obejrzany
    }

    suspend fun moveToWatchlist(movie: Movie) { // Przenosi film do listy do obejrzenia
        dao.insertMovie(movie.copy(isWatched = false)) // Zmienia status filmu na nieobejrzany
    }

    suspend fun deleteMovie(movie: Movie) { // Usuwa film z aplikacji
        dao.deleteMovie(movie) // Usuwa film z lokalnej bazy danych
    }
}