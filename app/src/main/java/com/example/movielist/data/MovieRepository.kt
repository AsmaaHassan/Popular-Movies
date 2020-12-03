package com.example.movielist.data

import android.content.Context
import android.util.Log
import com.example.movielist.data.internal.room.FavEntity
import com.example.movielist.data.internal.room.MovieEntity
import com.example.movielist.data.internal.room.MovieRoomDatabase
import com.example.movielist.data.remote.RemoteDataSource
import com.example.movielist.models.MovieItem
import com.example.movielist.models.MoviesList
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable
import kotlin.collections.ArrayList

class MovieRepository(
    private val remoteDataSource: RemoteDataSource,
    private val movieRoomDatabase: MovieRoomDatabase,
    private val context: Context
) {
    val TAG = "MovieRepository"

    //********Remote APIs********************
    fun getPopularMovies(page: Int): Observable<MoviesList> {
        return remoteDataSource.getMovies(page)
    }

    fun searchMovies(searchQuery: String, page: Int): Observable<MoviesList> {
        return remoteDataSource.searchMovies(searchQuery, page)
    }


    //***************Room****************
    fun isFavMovie(id: Int): Observable<Boolean> {
        return movieRoomDatabase.movieDAO().isFavouriteMovie(id)
    }


    fun getFirstPageRoom(): Observable<ArrayList<MovieItem>> {
        val moviesList: ArrayList<MovieItem> = ArrayList()
        movieRoomDatabase.movieDAO().getFirstPage().doOnNext(Consumer { moviesEntity ->
            if (moviesEntity?.size != 0) {
                for (i in moviesEntity.indices) {
                    val movieItem: MovieItem = MovieItem()
                    //            moviesEntity[i]
                    movieItem.id = moviesEntity[i].id
                    movieItem.title = moviesEntity[i].name
                    movieItem.voteAverage = moviesEntity[i].rating
                    movieItem.posterPath = moviesEntity[i].imagePath
                    movieItem.overview = moviesEntity[i].overview
                    moviesList.add(movieItem)

                }
            }
        })
            .doOnError(Consumer { error ->
                Log.i("error", "error" + error)
            })
            .subscribe()

        return Observable.fromArray(moviesList)
    }


    fun insertFirstPageMovies(moviesList: ArrayList<MovieItem>?) {
        if (moviesList != null) {
            if (moviesList.size != 0) {
                for (i in 0 until moviesList.size) {
                    var movieEntity: MovieEntity = MovieEntity(
                        moviesList[i].id,
                        moviesList[i].title,
                        moviesList[i].posterPath,
                        moviesList[i].voteAverage,
                        moviesList[i].overview,
                        is_firstPage = 1,
                        is_favourite = 0
                    )
                    Observable.fromCallable(Callable {
                        movieRoomDatabase.movieDAO().insert(movieEntity)
                    })
                        .subscribeOn(Schedulers.io())
                        .subscribe()
                }
            }
        }
    }


    fun insertFavMovie(movieItem: MovieItem?) {
        if (movieItem != null) {
            var movieEntity: FavEntity = FavEntity(
                movieItem.id,
                movieItem.title,
                movieItem.posterPath,
                movieItem.voteAverage,
                movieItem.overview,
                is_firstPage = 0,
                is_favourite = 1
            )
            Observable.fromCallable(Callable {
                movieRoomDatabase.movieDAO().insertFav(movieEntity)
            })
                .subscribeOn(Schedulers.io())
                .subscribe()
        }

    }


    fun removeMovie(id: Int) {

        Observable.fromCallable(Callable {
            movieRoomDatabase.movieDAO().deleteMovie(id)
        })
            .subscribeOn(Schedulers.io())
            .subscribe()
    }


    fun getFavouriteMovies(): Observable<ArrayList<MovieItem>> {
        val moviesList: ArrayList<MovieItem> = ArrayList()
        movieRoomDatabase.movieDAO().getFavouriteMovies()
            .subscribe({ favEntity ->
                if (favEntity?.size != 0) {
                    for (i in favEntity.indices) {
                        val movieItem = MovieItem()
                        //            moviesEntity[i]
                        movieItem.id = favEntity[i].id
                        movieItem.title = favEntity[i].name
                        movieItem.voteAverage = favEntity[i].rating
                        movieItem.posterPath = favEntity[i].imagePath
                        movieItem.overview = favEntity[i].overview
                        moviesList.add(movieItem)
                    }
                }
            }, { throwable -> //handle error
                Log.i(TAG, "getIsFavMovie() - error: " + throwable.message)
            })
        return Observable.fromArray(moviesList)
    }


    fun removeFirstPageMovies() {
        Observable.fromCallable(Callable {
            movieRoomDatabase.movieDAO().deleteFirstPageMovies()
        })
            .subscribeOn(Schedulers.io())
            .subscribe()
    }
}
