package com.example.moviesapp.data.remote

import com.example.moviesapp.data.model.MovieList
import com.example.moviesapp.repository.WebService

class RemoteMovieDataSource(private val webService: WebService) {

    suspend fun getUpcomingMovies(): MovieList = webService.getUpcomingMovies(com.example.moviesapp.application.AppConstants.API_KEY)

    suspend fun getTopRatedMovies(): MovieList = webService.getTopRatedMovies(com.example.moviesapp.application.AppConstants.API_KEY)

    suspend fun getPopularMovies(): MovieList = webService.getPopularMovies(com.example.moviesapp.application.AppConstants.API_KEY)

}