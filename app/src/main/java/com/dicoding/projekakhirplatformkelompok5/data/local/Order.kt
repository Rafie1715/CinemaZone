package com.dicoding.projekakhirplatformkelompok5.data.local

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    @get:Exclude var id: String = "",
    val userId: String = "",
    val movieTitle: String = "",
    val quantity: Int = 0,
    val seat: String = "",
    val orderDate: String = "",
    val location: String = "",
    val showDate: String = "",
    val showTime: String = "",
    val studio: String = "",
    val pricePerTicket: Int = 0
) : Parcelable