package com.dicoding.projekakhirplatformkelompok5.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Showtime(
    @SerializedName("location")
    val location: String,

    @SerializedName("price")
    val price: Int,

    @SerializedName("shows")
    val shows: List<Show>
) : Parcelable

@Parcelize
data class Show(
    @SerializedName("date")
    val date: String,

    @SerializedName("times")
    val times: List<TimeSlot>
) : Parcelable

@Parcelize
data class TimeSlot(
    @SerializedName("time")
    val time: String,

    @SerializedName("studio")
    val studio: String
) : Parcelable