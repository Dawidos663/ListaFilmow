package com.example.listafilmow.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listafilmow.ui.navigation.AppNavigation
import com.example.listafilmow.ui.viewmodel.MovieViewModel

// Główny kontener interfejsu użytkownika aplikacji.
// Funkcja uruchamia nawigację między ekranami,
// dzięki czemu MainActivity nie zawiera szczegółów dotyczących struktury UI.
@Composable
fun Root() {
    val movieViewModel: MovieViewModel = viewModel()

    AppNavigation(
        movieViewModel = movieViewModel
    )
}