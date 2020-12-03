package com.example.movielist.ui.moviedetails

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.movielist.MainActivity
import com.example.movielist.R
import com.example.movielist.models.MovieItem
import com.example.movielist.viewmodel.MovieViewModelFactory
import com.example.movielist.viewmodel.PopularMoviesViewModel
import kotlinx.android.synthetic.main.fragment_details.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.support.kodein
import org.kodein.di.generic.instance


class DetailsFragment : Fragment(), KodeinAware {
    val TAG = "DetailsFragment"

    //data
    var isFavMovie: Boolean = false
    var movie: MovieItem = MovieItem()

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
        return inflater.inflate(R.layout.fragment_details, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUI()
        setFavClickListener()
    }


    private fun getIsFavMovie() {
        if (movie.id != null) {
            movieViewModel.isFavMovie(movie.id!!)
                .subscribe({ isFav ->
                    isFavMovie = isFav
                    updateIesFavUI()
                }, { throwable -> //handle error
                    Log.i(TAG, "getIsFavMovie() - error: " + throwable.message)
                })

        }
    }


    private fun updateIesFavUI() {
        if (isFavMovie) {
            imDetailsFavMovie.setImageDrawable(activity?.getDrawable(R.drawable.ic_star))
        } else {
            imDetailsFavMovie.setImageDrawable(activity?.getDrawable(R.drawable.ic_star_off))
        }
    }


    private fun setUI() {
        movieViewModel.liveSelectedMovie.observe(viewLifecycleOwner, Observer { movieItem ->
            movie = movieItem
            if (movieItem != null) {
                updateUI(movieItem)
                //check movie is favourite
                getIsFavMovie()
            }
        })

    }


    private fun updateUI(movieItem: MovieItem) {
        //set action bar title
        (activity as MainActivity?)?.setActionBarTitle(movieItem.title)

        tvName_movieDetails.text = movieItem.title
        tvRating_movieDetails.text = movieItem.voteAverage.toString()
        tvOverview_movieDetails.text = movieItem.overview

        Glide.with(this)
            .load("http://image.tmdb.org/t/p/w500${movieItem?.posterPath}")
            .into(imMovieDetails)
    }


    private fun setFavClickListener() {
        Log.i(TAG, "setFavClickListener() - iFav: $isFavMovie")
        imDetailsFavMovie.setOnClickListener(View.OnClickListener {
            if (movie.id != null) {
                if (isFavMovie) {
                    movieViewModel.removeMovie(movie.id!!)
                    imDetailsFavMovie.setImageDrawable(activity?.getDrawable(R.drawable.ic_star_off))
                    isFavMovie = false
                } else {
                    //add to fav movies
                    movieViewModel.addFavourite(movie)
                    imDetailsFavMovie.setImageDrawable(activity?.getDrawable(R.drawable.ic_star))
                    isFavMovie = true
                }
            }
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        val item: MenuItem = menu.findItem(R.id.new_search)
        if (item != null) item.setVisible(false)
        menu.clear();
    }


}
