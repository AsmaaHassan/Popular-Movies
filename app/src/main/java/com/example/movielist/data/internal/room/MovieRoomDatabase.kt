package com.example.movielist.data.internal.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(MovieEntity::class, FavEntity::class), version = 5, exportSchema = false)
public abstract class MovieRoomDatabase : RoomDatabase() {
    abstract fun movieDAO(): MoviesDao

    companion object {
        @Volatile
        private var INSTANCE: MovieRoomDatabase? = null

        fun getDatabase(context: Context): MovieRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MovieRoomDatabase::class.java,
                    "movie_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}