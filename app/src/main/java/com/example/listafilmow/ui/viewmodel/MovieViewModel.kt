package com.example.listafilmow.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.listafilmow.R
import com.example.listafilmow.data.Movie
import com.example.listafilmow.data.MovieDatabase
import com.example.listafilmow.data.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface SearchState { // Opisuje możliwe stany wyszukiwania filmu
    object Idle : SearchState // Stan początkowy przed rozpoczęciem wyszukiwania
    object Loading : SearchState // Stan ładowania podczas pobierania danych
    data class Success(val movie: Movie) : SearchState // Stan sukcesu z pobranym filmem
    data class Error(val message: String) : SearchState // Stan błędu z komunikatem dla użytkownika
}

class MovieViewModel(application: Application) : AndroidViewModel(application) { // ViewModel przechowujący dane i logikę aplikacji

    private val repository = MovieRepository( // Tworzy repozytorium do obsługi danych
        MovieDatabase.getDatabase(application).movieDao() // Pobiera bazę danych i przekazuje DAO do repozytorium
    )

    val watchlist: StateFlow<List<Movie>> = repository.watchlist.stateIn( // Lista filmów do obejrzenia jako stan dla UI
        scope = viewModelScope, // Przepływ działa w zakresie życia ViewModelu
        started = SharingStarted.WhileSubscribed(5000), // Dane są aktywne gdy ekran ich używa i jeszcze przez 5 sekund
        initialValue = emptyList() // Początkowo lista jest pusta
    )

    val watched: StateFlow<List<Movie>> = repository.watched.stateIn( // Lista obejrzanych filmów jako stan dla UI
        scope = viewModelScope, // Przepływ działa w zakresie życia ViewModelu
        started = SharingStarted.WhileSubscribed(5000), // Dane są aktywne gdy ekran ich używa i jeszcze przez 5 sekund
        initialValue = emptyList() // Początkowo lista jest pusta
    )

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Idle) // Prywatny zmienny stan wyszukiwania
    val searchState: StateFlow<SearchState> = _searchState // Publiczny tylko do odczytu stan wyszukiwania dla UI

    fun searchMovie(title: String) { // Funkcja wyszukująca film po tytule
        if (title.isBlank()) return // Kończy działanie jeśli tytuł jest pusty

        viewModelScope.launch { // Uruchamia operację asynchronicznie w ViewModelu
            _searchState.value = SearchState.Loading // Ustawia stan ładowania

            try { // Próbuje wykonać wyszukiwanie filmu
                val movie = repository.searchMovie(title.trim()) // Wysyła oczyszczony tytuł do repozytorium

                if (movie != null) { // Sprawdza czy film został znaleziony
                    _searchState.value = SearchState.Success(movie) // Ustawia stan sukcesu z filmem
                } else { // Wykonuje się gdy film nie został znaleziony
                    _searchState.value = SearchState.Error( // Ustawia stan błędu
                        getApplication<Application>().getString(R.string.movie_not_found) // Pobiera komunikat że filmu nie znaleziono
                    )
                }
            } catch (e: Exception) { // Obsługuje błędy np problem z internetem
                _searchState.value = SearchState.Error( // Ustawia stan błędu
                    getApplication<Application>().getString( // Pobiera komunikat błędu z zasobów
                        R.string.connection_error, // Tekst błędu połączenia
                        e.message ?: "" // Dodaje wiadomość wyjątku jeśli istnieje
                    )
                )
            }
        }
    }

    fun addToWatchlist(movie: Movie) { // Dodaje film do listy do obejrzenia
        viewModelScope.launch { // Uruchamia zapis w tle
            repository.addToWatchlist(movie) // Wywołuje dodanie filmu w repozytorium
        }
    }

    fun markAsWatched(movie: Movie) { // Oznacza film jako obejrzany
        viewModelScope.launch { // Uruchamia aktualizację w tle
            repository.markAsWatched(movie) // Wywołuje zmianę statusu filmu w repozytorium
        }
    }

    fun moveToWatchlist(movie: Movie) { // Przenosi film z obejrzanych do listy do obejrzenia
        viewModelScope.launch { // Uruchamia aktualizację w tle
            repository.moveToWatchlist(movie) // Wywołuje zmianę statusu filmu w repozytorium
        }
    }

    fun deleteMovie(movie: Movie) { // Usuwa film z aplikacji
        viewModelScope.launch { // Uruchamia usuwanie w tle
            repository.deleteMovie(movie) // Wywołuje usunięcie filmu w repozytorium
        }
    }
}