package com.example.movielist

import android.app.Application
import android.util.Log.e
import com.example.movielist.data.MovieRepository
import com.example.movielist.data.internal.room.MovieRoomDatabase
import com.example.movielist.data.internal.room.MoviesDao
import com.example.movielist.data.remote.RemoteDataSource


import com.example.movielist.data.remote.RetrofitProvider
import com.example.movielist.viewmodel.MovieViewModelFactory
import io.reactivex.plugins.RxJavaPlugins
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider

import org.kodein.di.generic.singleton
import retrofit2.Retrofit


class MoviesListApp : Application(), KodeinAware {
    override fun onCreate() {
        super.onCreate()
    }

    override val kodein by Kodein.lazy {
        bind<Retrofit>() with singleton { RetrofitProvider.getInstance() }
        bind() from singleton { RemoteDataSource(instance()) }
        bind() from singleton { MovieRoomDatabase.getDatabase(this@MoviesListApp) }

        bind() from singleton { MovieRepository(instance(), instance(), this@MoviesListApp) }

        bind() from provider {
            MovieViewModelFactory(instance(), this@MoviesListApp)
        }
    }
}