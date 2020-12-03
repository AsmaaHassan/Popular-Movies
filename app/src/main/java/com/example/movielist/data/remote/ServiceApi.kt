package com.example.movielist.data.remote

import com.example.movielist.models.MoviesList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

import io.reactivex.Observable
import retrofit2.http.Path
import retrofit2.http.Url


interface ServiceApi {

    @GET("/3/movie/popular")
    fun getPopularMovies(@Query("page") page: Int): Observable<MoviesList>

    @GET("/3/search/movie")
    fun searchMovies(@Query("query", encoded = true) query: String, @Query("page") page: Int): Observable<MoviesList>
}