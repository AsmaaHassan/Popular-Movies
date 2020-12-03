package com.example.movielist.data.internal.room

import io.reactivex.Observable
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MoviesDao {

    @Query("SELECT * FROM movie_table")
    fun getFirstPage(): Observable<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(movieEntity: MovieEntity): Long

    @Query("DELETE FROM movie_table WHERE id = :id")
    fun deleteMovie(id: Int)

    @Query("DELETE FROM movie_table WHERE is_firstPage = 1 AND is_favourite = 0")
    fun deleteFirstPageMovies()


    @Query("SELECT * FROM fav_table WHERE is_favourite = 1")
    fun getFavouriteMovies(): Observable<List<MovieEntity>>

    @Query("SELECT is_favourite FROM fav_table WHERE id = :id")
    fun isFavouriteMovie(id: Int): Observable<Boolean>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFav(favEntity: FavEntity): Long

    @Query("DELETE FROM fav_table WHERE id = :id")
    fun deleteFav(id: Int)

}