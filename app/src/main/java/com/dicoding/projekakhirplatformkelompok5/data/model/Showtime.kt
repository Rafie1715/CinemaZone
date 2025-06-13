package com.dicoding.projekakhirplatformkelompok5.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Showtime(
    val location: String = "",
    val price: Int = 0,
    val shows: List<Show> = emptyList()
) : Parcelable

@Parcelize
data class Show(
    val date: String = "",
    val times: List<TimeSlot> = emptyList()
) : Parcelable

@Parcelize
data class TimeSlot(
    val time: String = "",
    val studio: String = ""
) : Parcelable