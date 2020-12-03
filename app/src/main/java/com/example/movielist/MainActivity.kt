package com.example.movielist

import com.example.movielist.R
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.movielist.ui.popularmovies.PopularMoviesFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            navigateToResult(false, "")
        }
        // Verify the action and get the query
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
//                doMySearch(query)
                navigateToResult(true, query)
            }
        }


    }


    private fun navigateToResult(isSearch: Boolean, searchQuery: String) {
        if (isSearch) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PopularMoviesFragment.newInstance(isSearch, searchQuery))
                .commitNow()
        } else {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, PopularMoviesFragment.newInstance(isSearch, searchQuery))
                .commitNow()
        }

    }

    public fun setActionBarTitle(title: String?) {
        supportActionBar?.title = title
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.new_search -> {
                onSearchRequested()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
