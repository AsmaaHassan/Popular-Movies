package com.example.movielist.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.movielist.data.MovieRepository
import com.example.movielist.data.remote.RemoteDataSource


class MovieViewModelFactory(private val movieRepository: MovieRepository, private val application: Application) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PopularMoviesViewModel(movieRepository, application) as T
    }
}