package com.example.listafilmow.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.listafilmow.R
import com.example.listafilmow.ui.screens.SearchScreen
import com.example.listafilmow.ui.screens.WatchedScreen
import com.example.listafilmow.ui.screens.WatchlistScreen
import com.example.listafilmow.ui.viewmodel.MovieViewModel

sealed class Screen( // Klasa opisująca dostępne ekrany aplikacji
    val route: String // Unikalna nazwa trasy używana przez nawigację
) {
    object Watchlist : Screen("watchlist")
    object Search : Screen("search")
    object Watched : Screen("watched")
}

@Composable
fun AppNavigation(
    movieViewModel: MovieViewModel // ViewModel przekazywany do ekranów żeby mogły korzystać z danych i funkcji
) {
    val navController = rememberNavController() // Kontroler zarządzający przechodzeniem między ekranami
    val backStackEntry by navController.currentBackStackEntryAsState() // Aktualny wpis stosu nawigacji czyli obecnie otwarty ekran
    val currentRoute = backStackEntry?.destination?.route // Trasa aktualnie wybranego ekranu

    val screens = listOf( // Lista ekranów widocznych w dolnym pasku nawigacji
        Screen.Watchlist,
        Screen.Search,
        Screen.Watched
    )

    Scaffold( // Główny układ ekranu z miejscem na dolny pasek i treść
        bottomBar = { // Definicja dolnego paska nawigacji
            NavigationBar { // Pasek nawigacyjny na dole ekranu
                screens.forEach { screen -> // Przechodzi po każdym ekranie i tworzy dla niego zakładkę

                    val label = when (screen) { // Wybiera tekst etykiety dla danej zakładki
                        Screen.Watchlist -> stringResource(R.string.screen_watchlist)
                        Screen.Search -> stringResource(R.string.screen_search)
                        Screen.Watched -> stringResource(R.string.screen_watched)
                    }

                    val iconDescription = when (screen) { // Wybiera opis ikony dla dostępności
                        Screen.Watchlist -> stringResource(R.string.icon_watchlist)
                        Screen.Search -> stringResource(R.string.icon_search)
                        Screen.Watched -> stringResource(R.string.icon_watched)
                    }

                    NavigationBarItem( // Pojedyncza zakładka w dolnym pasku
                        selected = currentRoute == screen.route, // Sprawdza czy ta zakładka jest aktualnie wybrana
                        onClick = {
                            navController.navigate(screen.route) { // Przechodzi do ekranu przypisanego do zakładki
                                launchSingleTop = true // Nie dodaje ponownie tego samego ekranu na stos
                            }
                        },
                        icon = { // Określa ikonę zakładki
                            when (screen) { // Dobiera ikonę do konkretnego ekranu
                                Screen.Watchlist -> Icon( // Ikona dla listy do obejrzenia
                                    Icons.Default.Bookmark, // Symbol zakładki
                                    contentDescription = iconDescription // Opis ikony dla czytników ekranu
                                )

                                Screen.Search -> Icon( // Ikona dla wyszukiwarki
                                    Icons.Default.Search, // Symbol lupy
                                    contentDescription = iconDescription // Opis ikony dla czytników ekranu
                                )

                                Screen.Watched -> Icon( // Ikona dla obejrzanych filmów
                                    Icons.Default.CheckCircle, // Symbol zatwierdzenia
                                    contentDescription = iconDescription // Opis ikony dla czytników ekranu
                                )
                            }
                        },
                        label = { // Określa podpis pod ikoną
                            Text(label) // Wyświetla nazwę zakładki
                        }
                    )
                }
            }
        }
    ) { padding -> // Przekazuje odstępy żeby treść nie nachodziła na dolny pasek
        NavHost( // Kontener który wyświetla odpowiedni ekran na podstawie trasy
            navController = navController, // Kontroler używany do zmiany ekranów
            startDestination = Screen.Watchlist.route, // Ekran startowy po uruchomieniu aplikacji
            modifier = Modifier.padding(padding) // Dodaje odstęp od dolnego paska
        ) {
            composable(Screen.Watchlist.route) { // Definicja trasy dla ekranu listy do obejrzenia
                WatchlistScreen(movieViewModel) // Wyświetla ekran listy do obejrzenia
            }

            composable(Screen.Search.route) { // Definicja trasy dla ekranu wyszukiwania
                SearchScreen(movieViewModel) // Wyświetla ekran wyszukiwania filmów
            }

            composable(Screen.Watched.route) { // Definicja trasy dla ekranu obejrzanych
                WatchedScreen(movieViewModel) // Wyświetla ekran filmów obejrzanych
            }
        }
    }
}