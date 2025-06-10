package com.dicoding.projekakhirplatformkelompok5.data.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val id: Long = 0,
    val userId: String,
    val movieTitle: String,
    val quantity: Int,
    val seat: String,
    val orderDate: String, // Tanggal kapan pesanan dibuat
    val location: String,
    val showDate: String, // Tanggal kapan filmnya tayang
    val showTime: String,
    val studio: String,
    val pricePerTicket: Int
) : Parcelable