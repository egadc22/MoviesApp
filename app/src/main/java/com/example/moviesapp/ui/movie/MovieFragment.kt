package com.example.moviesapp.ui.movie

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.example.moviesapp.*
import com.example.moviesapp.data.local.AppDatabase
import com.example.moviesapp.data.local.LocalMovieDataSource
import com.example.moviesapp.data.model.Movie
import com.example.moviesapp.data.remote.RemoteMovieDataSource
import com.example.moviesapp.databinding.FragmentMovieBinding
import com.example.moviesapp.presentation.MovieViewModel
import com.example.moviesapp.presentation.MovieViewModelFactory
import com.example.moviesapp.repository.MovieRepositoryImpl
import com.example.moviesapp.repository.RetrofitClient
import com.example.moviesapp.ui.movie.adapters.MovieAdapter
import com.example.moviesapp.ui.movie.adapters.concat.PopularConcatAdapter
import com.example.moviesapp.ui.movie.adapters.concat.TopRatedConcatAdapter
import com.example.moviesapp.ui.movie.adapters.concat.UpcomingConcatAdapter

class MovieFragment : Fragment(R.layout.fragment_movie), MovieAdapter.OnMovieClickListener {

    private lateinit var binding: FragmentMovieBinding
    private val viewModel by viewModels<MovieViewModel> {
        MovieViewModelFactory(
            MovieRepositoryImpl(
                RemoteMovieDataSource(RetrofitClient.webservice),
                LocalMovieDataSource(AppDatabase.getDatabase(requireContext()).movieDao())
            )
        )
    }
    private lateinit var concatAdapter: ConcatAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMovieBinding.bind(view)

        concatAdapter = ConcatAdapter()

        viewModel.fetchMainScreenMovies().observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is com.example.moviesapp.core.Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is com.example.moviesapp.core.Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    concatAdapter.apply {
                        addAdapter(
                            0,
                            UpcomingConcatAdapter(
                                MovieAdapter(
                                    result.data.first.results,
                                    this@MovieFragment
                                )
                            )
                        )
                        addAdapter(
                            1,
                            TopRatedConcatAdapter(
                                MovieAdapter(
                                    result.data.second.results,
                                    this@MovieFragment
                                )
                            )
                        )
                        addAdapter(
                            2,
                            PopularConcatAdapter(
                                MovieAdapter(
                                    result.data.third.results,
                                    this@MovieFragment
                                )
                            )
                        )
                    }

                    binding.rvMovies.adapter = concatAdapter
                }
                is com.example.moviesapp.core.Resource.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    Log.d("Error", "${result.exception}")
                }
            }
        })

    }


    override fun onMovieClick(movie: Movie) {
        val action = MovieFragmentDirections.actionMovieFragmentToMovieDetailFragment(

            movie.poster_path,
            movie.backdrop_path!!,
            movie.vote_average.toFloat(),
            movie.vote_count,
            movie.overview,
            movie.title,
            movie.original_language,
            movie.release_date
        )
        findNavController().navigate(action)
    }
}