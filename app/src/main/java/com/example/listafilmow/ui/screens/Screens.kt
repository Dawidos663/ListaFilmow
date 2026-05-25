package com.example.listafilmow.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.listafilmow.R
import com.example.listafilmow.data.Movie
import com.example.listafilmow.ui.viewmodel.MovieViewModel
import com.example.listafilmow.ui.viewmodel.SearchState

@OptIn(ExperimentalMaterial3Api::class) // Pozwala używać eksperymentalnych elementów Material 3
@Composable
fun WatchlistScreen(
    viewModel: MovieViewModel
) {
    val movies by viewModel.watchlist.collectAsState() // Pobiera listę filmów do obejrzenia jako stan Compose

    Scaffold( // Główny układ ekranu
        topBar = { // Miejsce na górny pasek aplikacji
            LargeTopAppBar( // Duży górny pasek z tytułem
                title = { // Definicja tytułu paska
                    Text(stringResource(R.string.title_watchlist)) // Wyświetla tytuł ekranu z zasobów
                }
            )
        }
    ) { padding -> // Przekazuje odstępy od elementów Scaffold
        MovieList( // Wyświetla listę filmów
            movies = movies, // Przekazuje filmy do obejrzenia
            emptyText = stringResource(R.string.empty_watchlist), // Tekst gdy lista jest pusta
            modifier = Modifier.padding(padding), // Dodaje odstęp od górnego paska
            primaryButtonText = stringResource(R.string.button_mark_watched), // Tekst przycisku oznaczania jako obejrzane
            onPrimaryClick = { movie -> // Akcja po kliknięciu głównego przycisku
                viewModel.markAsWatched(movie) // Oznacza film jako obejrzany
            },
            onDeleteClick = { movie -> // Akcja po kliknięciu usuń
                viewModel.deleteMovie(movie) // Usuwa film z bazy
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchedScreen(
    viewModel: MovieViewModel
) {
    val movies by viewModel.watched.collectAsState() // Pobiera listę obejrzanych filmów jako stan Compose

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(stringResource(R.string.title_watched))
                }
            )
        }
    ) { padding ->
        MovieList(
            movies = movies,
            emptyText = stringResource(R.string.empty_watched),
            modifier = Modifier.padding(padding),
            primaryButtonText = stringResource(R.string.button_move_to_watchlist),
            onPrimaryClick = { movie ->
                viewModel.moveToWatchlist(movie) // Przenosi film do listy do obejrzenia
            },
            onDeleteClick = { movie ->
                viewModel.deleteMovie(movie) // Usuwa film z bazy
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: MovieViewModel
) {
    var title by remember { mutableStateOf("") } // Przechowuje wpisany tytuł filmu
    val searchState by viewModel.searchState.collectAsState()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(stringResource(R.string.title_search))
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize() // Zajmuje cały dostępny ekran
                .padding(padding) // Uwzględnia odstęp od górnego paska
                .padding(16.dp), // Dodaje wewnętrzny odstęp
            verticalArrangement = Arrangement.spacedBy(16.dp) // Ustawia odstępy między elementami
        ) {
            OutlinedTextField( // Pole tekstowe do wpisania tytułu
                value = title, // Aktualna wartość pola
                onValueChange = { title = it }, // Aktualizuje tytuł po wpisaniu tekstu
                label = { // Etykieta pola tekstowego
                    Text(stringResource(R.string.movie_title_label))
                },
                modifier = Modifier.fillMaxWidth(), // Pole zajmuje całą szerokość
                singleLine = true // Ogranicza wpisywanie do jednej linii
            )

            Button( // Przycisk wyszukiwania
                onClick = {
                    viewModel.searchMovie(title) // Wywołuje wyszukiwanie filmu po tytule
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.button_search))
            }

            when (val state = searchState) { // Sprawdza aktualny stan wyszukiwania
                SearchState.Idle -> { // Stan początkowy bez wyszukiwania
                    Text(stringResource(R.string.search_instruction)) // Wyświetla instrukcję dla użytkownika
                }

                SearchState.Loading -> { // Stan ładowania danych
                    CircularProgressIndicator() // Pokazuje animację ładowania
                }

                is SearchState.Error -> { // Stan błędu wyszukiwania
                    Text( // Wyświetla komunikat błędu
                        text = state.message, // Treść błędu
                        color = MaterialTheme.colorScheme.error // Kolor błędu z motywu
                    )
                }

                is SearchState.Success -> { // Stan poprawnego znalezienia filmu
                    MovieCard( // Wyświetla kartę znalezionego filmu
                        movie = state.movie, // Przekazuje znaleziony film
                        primaryButtonText = stringResource(R.string.button_add_to_watchlist), // Tekst przycisku dodania do listy
                        onPrimaryClick = { // Akcja po kliknięciu głównego przycisku
                            viewModel.addToWatchlist(state.movie) // Dodaje film do listy do obejrzenia
                        },
                        onDeleteClick = null // Brak przycisku usuwania na ekranie wyszukiwania
                    )
                }
            }
        }
    }
}

@Composable // Funkcja tworząca listę filmów
fun MovieList(
    movies: List<Movie>, // Lista filmów do wyświetlenia
    emptyText: String, // Tekst pokazywany gdy lista jest pusta
    modifier: Modifier = Modifier, // Modyfikator wyglądu z domyślną wartością
    primaryButtonText: String, // Tekst głównego przycisku na karcie
    onPrimaryClick: (Movie) -> Unit, // Funkcja wykonywana po kliknięciu głównego przycisku
    onDeleteClick: (Movie) -> Unit // Funkcja wykonywana po kliknięciu usuń
) {
    if (movies.isEmpty()) { // Sprawdza czy lista filmów jest pusta
        Column( // Układa tekst pionowo
            modifier = modifier // Używa przekazanego modyfikatora
                .fillMaxSize() // Zajmuje cały ekran
                .padding(16.dp) // Dodaje odstęp wewnętrzny
        ) {
            Text(emptyText) // Wyświetla informację o pustej liście
        }
    } else { // Wykonuje się gdy lista nie jest pusta
        LazyColumn(
            modifier = modifier.fillMaxSize(), // Lista zajmuje cały ekran
            contentPadding = PaddingValues(16.dp), // Dodaje odstęp wokół zawartości
            verticalArrangement = Arrangement.spacedBy(12.dp) // Dodaje odstępy między kartami
        ) {
            items(movies) { movie -> // Tworzy element listy dla każdego filmu
                MovieCard( // Wyświetla pojedynczy film jako kartę
                    movie = movie, // Przekazuje aktualny film
                    primaryButtonText = primaryButtonText, // Przekazuje tekst głównego przycisku
                    onPrimaryClick = { // Akcja po kliknięciu głównego przycisku
                        onPrimaryClick(movie) // Wywołuje akcję dla danego filmu
                    },
                    onDeleteClick = { // Akcja po kliknięciu usuń
                        onDeleteClick(movie) // Wywołuje usunięcie dla danego filmu
                    }
                )
            }
        }
    }
}

@Composable // Funkcja tworząca kartę pojedynczego filmu
fun MovieCard(
    movie: Movie, // Film którego dane mają być wyświetlone
    primaryButtonText: String, // Tekst głównego przycisku
    onPrimaryClick: () -> Unit, // Akcja głównego przycisku
    onDeleteClick: (() -> Unit)? // Opcjonalna akcja usuwania
) {
    Card( // Karta wizualna filmu
        modifier = Modifier.fillMaxWidth() // Karta zajmuje całą szerokość
    ) {
        Column( // Układa zawartość karty pionowo
            modifier = Modifier.padding(16.dp), // Dodaje wewnętrzny odstęp w karcie
            verticalArrangement = Arrangement.spacedBy(12.dp) // Dodaje odstępy między elementami
        ) {
            Row( // Układa plakat i opis obok siebie
                modifier = Modifier.fillMaxWidth(), // Wiersz zajmuje całą szerokość
                horizontalArrangement = Arrangement.spacedBy(12.dp) // Dodaje odstęp między plakatem a opisem
            ) {
                if (movie.posterUrl.isNotBlank()) { // Sprawdza czy film ma link do plakatu
                    AsyncImage( // Ładuje i wyświetla obraz z internetu
                        model = movie.posterUrl, // Adres plakatu filmu
                        contentDescription = "Plakat filmu ${movie.title}", // Opis obrazka dla dostępności
                        modifier = Modifier // Modyfikator rozmiaru plakatu
                            .width(120.dp) // Ustawia szerokość plakatu
                            .height(180.dp), // Ustawia wysokość plakatu
                        contentScale = ContentScale.Crop // Kadruje obraz do podanego rozmiaru
                    )
                }

                Column( // Układa dane filmu pionowo
                    modifier = Modifier.weight(1f), // Zajmuje pozostałe miejsce obok plakatu
                    verticalArrangement = Arrangement.spacedBy(6.dp) // Dodaje odstępy między tekstami
                ) {
                    Text( // Wyświetla tytuł filmu
                        text = movie.title, // Tytuł filmu
                        style = MaterialTheme.typography.titleLarge // Styl dużego tytułu
                    )

                    if (movie.year.isNotBlank()) { // Sprawdza czy rok nie jest pusty
                        Text(stringResource(R.string.year_label, movie.year)) // Wyświetla rok filmu
                    }

                    if (movie.genre.isNotBlank()) { // Sprawdza czy gatunek nie jest pusty
                        Text(stringResource(R.string.genre_label, movie.genre)) // Wyświetla gatunek filmu
                    }

                    if (movie.rating.isNotBlank()) { // Sprawdza czy ocena nie jest pusta
                        Text(stringResource(R.string.rating_label, movie.rating)) // Wyświetla ocenę filmu
                    }

                    if (movie.plot.isNotBlank()) { // Sprawdza czy opis fabuły nie jest pusty
                        Text( // Wyświetla opis fabuły
                            text = movie.plot, // Treść opisu filmu
                            style = MaterialTheme.typography.bodyMedium // Styl zwykłego tekstu
                        )
                    }
                }
            }

            Row( // Układa przyciski poziomo
                modifier = Modifier.fillMaxWidth(), // Wiersz zajmuje całą szerokość
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Dodaje odstęp między przyciskami
            ) {
                Button( // Główny przycisk akcji
                    onClick = onPrimaryClick // Wywołuje przekazaną akcję
                ) {
                    Text(primaryButtonText) // Wyświetla tekst głównego przycisku
                }

                Spacer(modifier = Modifier.weight(1f)) // Wypycha przycisk usuwania na prawą stronę

                if (onDeleteClick != null) { // Sprawdza czy przycisk usuwania ma być widoczny
                    OutlinedButton( // Przycisk usuwania z obramowaniem
                        onClick = onDeleteClick // Wywołuje akcję usuwania
                    ) {
                        Text(stringResource(R.string.button_delete)) // Wyświetla tekst usuń
                    }
                }
            }
        }
    }
}