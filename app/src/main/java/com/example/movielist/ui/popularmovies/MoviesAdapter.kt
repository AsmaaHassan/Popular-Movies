package com.example.movielist.ui.popularmovies

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movielist.R
import com.example.movielist.models.MovieItem
import com.example.movielist.viewmodel.PopularMoviesViewModel

class MoviesAdapter(
    val moviesList: ArrayList<MovieItem>,
    private val onClickMovie: OnClickMovie,
    val context: Context?,
    val moviesViewModel: PopularMoviesViewModel
) :
    RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return MoviesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return moviesList.size
    }


    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        holder.itemView.setOnClickListener(View.OnClickListener {
            onClickMovie.clickMovie(moviesList[position])
        })

        return holder.bind(moviesList[position], moviesViewModel, context)
    }


    class MoviesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMovieName: TextView = itemView.findViewById(R.id.tvMovieName)
        private val imMovie: ImageView = itemView.findViewById(R.id.imMovie)
        private val tvMovieRating: TextView = itemView.findViewById(R.id.tvMovieRating)
        private val imFavMovie: ImageView = itemView.findViewById(R.id.imFavMovie)

        fun bind(movie: MovieItem, moviesViewModel: PopularMoviesViewModel, context: Context?) {
            Glide.with(itemView.context).load("http://image.tmdb.org/t/p/w500${movie.posterPath}")
                .into(imMovie)
            tvMovieName.text = movie.title
            tvMovieRating.text = movie.voteAverage.toString()
            tvMovieRating.text = movie.voteAverage.toString()
            if (movie.id != null) {
                moviesViewModel.isFavMovie(movie.id!!).doOnError { error ->
                }
                    .doOnNext { isFav ->
                        if (isFav) {
                            imFavMovie.setImageDrawable(context?.getDrawable(R.drawable.ic_star))
                        } else {
                            imFavMovie.setImageDrawable(context?.getDrawable(R.drawable.ic_star_off))
                        }
                    }
                    .subscribe()
            }
        }
    }


    fun addData(listItems: ArrayList<MovieItem>) {
        this.moviesList.addAll(listItems)
    }

}