package com.dicoding.projekakhirplatformkelompok5.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("overview")
    val overview: String = "",
    @SerializedName("poster_path")
    val posterPath: String? = null,
    @SerializedName("release_date")
    val releaseDate: String? = null,
    @SerializedName("vote_average")
    val voteAverage: Double? = 0.0,
    @SerializedName("trailer_url")
    val trailerUrl: String? = null,
    @SerializedName("genre")
    val genre: List<String>? = emptyList(),
    @SerializedName("showtimes")
    val showtimes: List<Showtime>? = emptyList()
) : Parcelable