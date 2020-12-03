package com.example.movielist.data.remote

import com.example.movielist.models.MoviesList
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import retrofit2.Retrofit


class RemoteDataSource(retrofit: Retrofit) {
    private val serviceApi: ServiceApi = retrofit.create(ServiceApi::class.java)

    fun getMovies(page: Int): Observable<MoviesList> {
        return serviceApi.getPopularMovies(page)
            .subscribeOn(Schedulers.io())
            .observeOn(
                AndroidSchedulers.mainThread()
            )
    }

    fun searchMovies(searchQuery: String, page: Int): Observable<MoviesList> {
        return serviceApi.searchMovies(searchQuery, page)
            .subscribeOn(Schedulers.io())
            .observeOn(
                AndroidSchedulers.mainThread()
            )
    }
}