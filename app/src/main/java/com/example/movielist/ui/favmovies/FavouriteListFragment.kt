package com.example.movielist.ui.favmovies

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movielist.MainActivity

import com.example.movielist.R
import com.example.movielist.models.MovieItem
import com.example.movielist.ui.moviedetails.DetailsFragment
import com.example.movielist.ui.popularmovies.MoviesAdapter
import com.example.movielist.ui.popularmovies.OnClickMovie
import com.example.movielist.viewmodel.MovieViewModelFactory
import com.example.movielist.viewmodel.PopularMoviesViewModel
import kotlinx.android.synthetic.main.fragment_favourite_list.*
import kotlinx.android.synthetic.main.main_fragment.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.support.kodein
import org.kodein.di.generic.instance


class FavouriteListFragment : Fragment(), KodeinAware, OnClickMovie {

    /*
    data
     */
    var movie: MovieItem = MovieItem()

    lateinit var moviesAdapter: MoviesAdapter

    //kodin
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
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //set action bar title
        (activity as MainActivity?)?.setActionBarTitle("Favourite Movies")

        initRecyclerView()

        //set observers
        setObservers()

        movieViewModel.getFavMoviesList()
    }


    private fun setObservers() {
        movieViewModel.liveDataFavMovies.observe(viewLifecycleOwner, Observer { favMovies ->
            moviesAdapter = MoviesAdapter(
                favMovies,
                this@FavouriteListFragment,
                context,
                moviesViewModel = movieViewModel
            )
            rvFavMovies.apply {
                adapter = moviesAdapter
            }
        })

        movieViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading)
                prgFavMovies.visibility = View.VISIBLE
            else prgFavMovies.visibility = View.GONE
        })


    }

    private fun initRecyclerView() {
        rvFavMovies.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
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

