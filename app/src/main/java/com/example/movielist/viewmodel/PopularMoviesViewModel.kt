package com.example.movielist.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.movielist.data.MovieRepository
import com.example.movielist.models.MovieItem
import com.example.movielist.models.MoviesList
import com.saraelmoghazy.base.util.ConnectivityUtil
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class PopularMoviesViewModel(

    private val movieRepository: MovieRepository,
    application: Application
) : AndroidViewModel(application) {
    companion object {
        val TAG = "PopularMoviesViewModel"
    }

    /**
    LiveData
     **/
    val liveDataMovies: MutableLiveData<ArrayList<MovieItem>> =
        MutableLiveData<ArrayList<MovieItem>>()

    val liveDataFavMovies: MutableLiveData<ArrayList<MovieItem>> =
        MutableLiveData<ArrayList<MovieItem>>()

    val isLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val liveSelectedMovie: MutableLiveData<MovieItem> = MutableLiveData<MovieItem>()
    val liveTotalPages: MutableLiveData<Int> = MutableLiveData<Int>()


    fun search(query: String?, page: Int) {
        val isConnected = ConnectivityUtil.isInternetAvailable(getApplication())
        if (isConnected) {
            if (query != null) {
                getSearchResult(query!!, page)
            } else {
                errorMessage.value = "please enter words"
            }
        } else {
            errorMessage.value = "Check internet connection"
        }
    }

    fun init(page: Int) {
        val isConnected = ConnectivityUtil.isInternetAvailable(getApplication())
        if (page == 1) {
            getFirstPageFromRoom()
        }
        if (isConnected) {
            getDataFromApi(page)

        } else {
            errorMessage.value = "Check internet connection"
        }
    }


    private fun getFirstPageFromRoom() {
        movieRepository.getFirstPageRoom().subscribe(Consumer { t ->
            liveDataMovies.value = t
        })
    }


    fun getDataFromApi(page: Int) {
        movieRepository.getPopularMovies(page)
            .subscribe(object : Observer<MoviesList> {
                override fun onSubscribe(d: Disposable) {
                    //Log.d(TAG,"on subscribe");
                    //Log.d(TAG,d.toString());
                    isLoading.value = true
                }

                override fun onNext(moviesList: MoviesList) {
                    liveTotalPages.value = moviesList.totalPages
                    liveDataMovies.value = moviesList.results
                    isLoading.value = false
                    //add data to room if page = 1
                    if (page == 1)
                        addFirstPageRoom(moviesList)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    errorMessage.value = e.message
                    isLoading.value = false
                }

                override fun onComplete() {}
            })
    }


    /*
     add first page data to Room database
     */
    fun addFirstPageRoom(moviesList: MoviesList) {
        //first delete existing first page
        movieRepository.removeFirstPageMovies()
        movieRepository.insertFirstPageMovies(moviesList.results)
    }

    /*
    Get is movie marked as favourite from in Room db
     */
    fun isFavMovie(id: Int): Observable<Boolean> {
        return movieRepository.isFavMovie(id).subscribeOn(Schedulers.io())
            .observeOn(
                AndroidSchedulers.mainThread()
            )
    }

    /*
    add movie to favourite list in Room db
     */
    fun addFavourite(movie: MovieItem) {
        movieRepository.insertFavMovie(movie)
    }

    /*
    remove movie from favourite list in Room db
     */
    fun removeMovie(id: Int) {
        movieRepository.removeMovie(id)
    }


    fun getFavMoviesList() {
        movieRepository.getFavouriteMovies().subscribe(Consumer { t ->
            liveDataFavMovies.value = t
        })
    }


    fun getSearchResult(searchQuery: String, page: Int) {
        movieRepository.searchMovies(searchQuery, page)
            .subscribe(object : Observer<MoviesList> {
                override fun onSubscribe(d: Disposable) {
                    //Log.d(TAG,"on subscribe");
                    //Log.d(TAG,d.toString());
                    isLoading.value = true
                }

                override fun onNext(moviesList: MoviesList) {
                    liveTotalPages.value = moviesList.totalPages
                    liveDataMovies.value = moviesList.results
                    isLoading.value = false

                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    errorMessage.value = e.message
                    isLoading.value = false
                }

                override fun onComplete() {}
            })
    }
}
