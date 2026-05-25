package com.example.listafilmow.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database( // Oznacza klasę jako bazę danych Room
    entities = [Movie::class], // Określa że baza przechowuje obiekty Movie
    version = 1 // Określa wersję bazy danych
)
abstract class MovieDatabase : RoomDatabase() { // Główna klasa lokalnej bazy danych

    abstract fun movieDao(): MovieDao // Udostępnia DAO do wykonywania operacji na filmach

    companion object { // Miejsce na elementy wspólne dla całej klasy
        @Volatile // Zapewnia widoczność INSTANCE między różnymi wątkami
        private var INSTANCE: MovieDatabase? = null // Przechowuje jedną wspólną instancję bazy danych

        fun getDatabase(context: Context): MovieDatabase { // Zwraca istniejącą bazę albo tworzy nową
            return INSTANCE ?: synchronized(this) { // Blokuje jednoczesne tworzenie bazy przez kilka wątków
                val database = Room.databaseBuilder( // Rozpoczyna budowanie bazy danych Room
                    context.applicationContext, // Używa kontekstu aplikacji żeby uniknąć wycieków pamięci
                    MovieDatabase::class.java, // Wskazuje klasę bazy danych
                    "movies_database" // Określa nazwę pliku bazy na urządzeniu
                )
                    .fallbackToDestructiveMigration() // Przy zmianie wersji bez migracji usuwa starą bazę i tworzy nową
                    .build() // Tworzy gotową instancję bazy danych

                INSTANCE = database // Zapisuje utworzoną bazę jako wspólną instancję

                database // Zwraca utworzoną bazę danych
            }
        }
    }
}