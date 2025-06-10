package com.dicoding.projekakhirplatformkelompok5.data.network

import com.dicoding.projekakhirplatformkelompok5.data.model.Movie
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("movies.json")
    suspend fun getAllMovies(): Response<List<Movie>>
}