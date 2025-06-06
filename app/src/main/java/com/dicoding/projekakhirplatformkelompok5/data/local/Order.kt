package com.dicoding.projekakhirplatformkelompok5.data.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val id: Long = 0, // Default value, SQLite will auto-generate if primary key
    val userId: String, // Email atau ID user yang login
    val movieTitle: String,
    val quantity: Int,
    val seat: String,
    val orderDate: String // Timestamp atau format tanggal
) : Parcelable