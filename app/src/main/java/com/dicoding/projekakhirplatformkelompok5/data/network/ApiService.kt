package com.dicoding.projekakhirplatformkelompok5.data.network

import com.dicoding.projekakhirplatformkelompok5.data.model.Movie
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    // GANTI "NAMA_USER_GITHUB", "NAMA_REPOSITORI", dan "NAMA_BRANCH"
    // Sesuaikan juga path jika movies.json tidak di root branch
    @GET("movies.json")
    suspend fun getAllMoviesDirect(): Response<List<Movie>>
}