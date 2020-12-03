package com.example.movielist.ui.popularmovies

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movielist.MainActivity
import com.example.movielist.R
import com.example.movielist.models.MovieItem
import com.example.movielist.ui.favmovies.FavouriteListFragment
import com.example.movielist.ui.moviedetails.DetailsFragment
import com.example.movielist.util.PaginationScrollListener
import com.example.movielist.viewmodel.MovieViewModelFactory
import com.example.movielist.viewmodel.PopularMoviesViewModel
import com.saraelmoghazy.base.util.ConnectivityUtil
import kotlinx.android.synthetic.main.main_fragment.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.support.kodein
import org.kodein.di.generic.instance


class PopularMoviesFragment() : Fragment(), KodeinAware, OnClickMovie {
    private val TAG = "PopularMoviesFragment"
    private var page: Int = 1
    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    lateinit var moviesAdapter: MoviesAdapter
    var isSearch: Boolean = false
    var searchQuery: String? = ""

    companion object {
        fun newInstance(is_search: Boolean, search_query: String) = PopularMoviesFragment().apply {
            arguments = Bundle().apply {
                putBoolean("is_search", is_search)
                putString("search_query", search_query)
            }
        }
    }


    /*
    init kodin( for depency injection)
     */

    override val kodein: Kodein by kodein()
    private val movieViewModelFactory: MovieViewModelFactory by instance()
    private val movieViewModel: PopularMoviesViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            movieViewModelFactory
        ).get(PopularMoviesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        page = 1

        //set action bar title
        (activity as MainActivity?)?.setActionBarTitle("Movies")

        initRecyclerView()

        //set observers
        setObservers()

        isSearch = arguments?.getBoolean("is_search", false) ?: false

        if (isSearch == false) {
            //get data for the first time
            movieViewModel.init(page)
        } else {
            searchQuery = arguments?.getString("search_query", "")
            movieViewModel.search(searchQuery, page)
        }
        fab.setOnClickListener(View.OnClickListener {
            //open fav fragment
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.container, FavouriteListFragment())?.addToBackStack("a")
            transaction?.commit()
        })

        setScrollListener()
    }


    private fun initRecyclerView() {
        rvMovies.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
        }
    }


    private fun setObservers() {
        movieViewModel.errorMessage.observe(viewLifecycleOwner, Observer { t ->
            Log.i(TAG, "error" + t)
            Toast.makeText(activity, t, Toast.LENGTH_SHORT).show()
            prgMovies.visibility = View.GONE
        })

        movieViewModel.liveTotalPages.observe(viewLifecycleOwner, Observer { totalPages ->
            if (totalPages == page)
                isLastPage = true
        })

        movieViewModel.liveDataMovies.observe(viewLifecycleOwner, Observer { moviesList ->
            if (page == 1) {
                moviesAdapter = MoviesAdapter(
                    moviesList,
                    this@PopularMoviesFragment,
                    context,
                    moviesViewModel = movieViewModel
                )
                rvMovies.apply {
                    adapter = moviesAdapter
                }
            } else {
                movieViewModel.liveDataMovies.value?.let { moviesAdapter.addData(moviesList) }
            }
        })

        movieViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading)
                prgMovies.visibility = View.VISIBLE
            else prgMovies.visibility = View.GONE
        })
    }


    private fun setScrollListener() {
        rvMovies?.addOnScrollListener(object :
            PaginationScrollListener(rvMovies.layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                movieViewModel.isLoading.value = true
                //you have to call loadmore items to get more data
                getMoreItems()
            }
        })
    }


    fun getMoreItems() {
        page++
        if (isSearch) {
            movieViewModel.search(searchQuery, page)
        } else {
            movieViewModel.getDataFromApi(page)
        }
    }


    override fun clickMovie(movieItem: MovieItem) {
        //set selected item in viewmodel to share it between fragments
        movieViewModel.liveSelectedMovie.value = movieItem

        //open details fragment
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.container, DetailsFragment())?.addToBackStack("a")

        transaction?.commit()
    }


}

