package com.dicoding.projekakhirplatformkelompok5.data.local

data class WishlistItem(
    val id: Int, // ID dari film itu sendiri (bukan ID primary key tabel wishlist)
    val userId: String, // Email atau ID user yang login
    val title: String, // Simpan judul untuk tampilan cepat
    val posterPath: String?, // Simpan poster path untuk tampilan cepat
    val addedDate: Long // Timestamp kapan ditambahkan ke wishlist
)