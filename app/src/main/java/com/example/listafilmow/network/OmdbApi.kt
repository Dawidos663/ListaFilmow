package com.example.listafilmow.network

import com.example.listafilmow.data.Movie
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "ab321a82"

interface OmdbApiService { // Interfejs opisujący zapytania do API OMDb

    @GET("/") // Wykonuje zapytanie GET na główny adres API
    suspend fun searchMovie( // Funkcja wyszukująca film po tytule
        @Query("t") title: String, // Parametr t zawiera tytuł filmu
        @Query("apikey") apiKey: String = API_KEY // Parametr apikey zawiera klucz API
    ): OmdbMovieResponse // Zwraca odpowiedź z API w formie obiektu OmdbMovieResponse
}

data class OmdbMovieResponse( // Klasa opisująca odpowiedź JSON z API OMDb
    @SerializedName("Title") // Łączy pole Title z JSON z polem title w Kotlinie
    val title: String = "",

    @SerializedName("Year")
    val year: String = "",

    @SerializedName("Genre")
    val genre: String = "",

    @SerializedName("Plot")
    val plot: String = "",

    @SerializedName("Poster")
    val poster: String = "",

    @SerializedName("imdbRating")
    val rating: String = "",

    @SerializedName("imdbID")
    val imdbId: String = "",

    @SerializedName("Response")
    val response: String = "False"
)

fun OmdbMovieResponse.toMovie(): Movie { // Zamienia odpowiedź z API na lokalny model Movie
    return Movie( // Tworzy obiekt Movie używany w aplikacji i bazie danych
        id = imdbId.ifBlank { title.hashCode().toString() }, // Ustawia id z IMDb albo tworzy je z tytułu
        title = title, // Przepisuje tytuł filmu
        year = year, // Przepisuje rok produkcji
        genre = genre, // Przepisuje gatunek filmu
        rating = rating, // Przepisuje ocenę filmu
        plot = plot, // Przepisuje opis fabuły
        posterUrl = if (poster != "N/A") poster else "" // Zapisuje plakat tylko jeśli API zwróciło prawidłowy adres
    )
}

object RetrofitInstance { // Obiekt przechowujący jedną instancję Retrofit
    val api: OmdbApiService by lazy { // Tworzy API dopiero przy pierwszym użyciu
        Retrofit.Builder() // Rozpoczyna budowanie klienta Retrofit
            .baseUrl("https://www.omdbapi.com/") // Ustawia bazowy adres API OMDb
            .addConverterFactory(GsonConverterFactory.create()) // Dodaje konwerter JSON na obiekty Kotlin
            .build() // Buduje instancję Retrofit
            .create(OmdbApiService::class.java) // Tworzy implementację interfejsu OmdbApiService
    }
}