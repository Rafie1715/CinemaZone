package com.dicoding.projekakhirplatformkelompok5.ui.wishlist

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.projekakhirplatformkelompok5.R
import com.dicoding.projekakhirplatformkelompok5.data.local.WishlistDatabaseHelper
import com.dicoding.projekakhirplatformkelompok5.data.model.Movie
import com.dicoding.projekakhirplatformkelompok5.data.network.ApiClient
import com.dicoding.projekakhirplatformkelompok5.databinding.FragmentWishlistBinding
import com.dicoding.projekakhirplatformkelompok5.ui.home.MovieAdapter
import com.dicoding.projekakhirplatformkelompok5.ui.home.MovieDetailDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class WishlistFragment : Fragment() {

    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!

    // Database helper untuk wishlist
    private lateinit var wishlistDbHelper: WishlistDatabaseHelper

    // Adapter untuk RecyclerView
    private lateinit var wishlistAdapter: MovieAdapter

    // List untuk menampung film yang di-wishlist
    private val wishlistedMovies = mutableListOf<Movie>()

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        // Inisialisasi Database Helper
        wishlistDbHelper = WishlistDatabaseHelper(requireContext())
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        // Muat atau refresh data setiap kali fragment ini menjadi terlihat oleh pengguna
        loadWishlistedMovies()
    }

    private fun setupRecyclerView() {
        wishlistAdapter = MovieAdapter(
            requireContext(),
            wishlistedMovies, // Adapter akan menggunakan list ini
            onMovieClickListener = { movie ->
                MovieDetailDialogFragment.newInstance(movie)
                    .show(parentFragmentManager, MovieDetailDialogFragment.TAG)
            },
            onWishlistClickListener = { movie, shouldBeWishlisted ->
                // Di halaman ini, klik ikon hati selalu berarti menghapus
                if (!shouldBeWishlisted) {
                    handleRemoveFromWishlist(movie)
                }
            }
        )
        binding.recyclerViewWishlist.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = wishlistAdapter
        }
    }

    /**
     * Fungsi utama untuk memuat film dari database SQLite
     * dan memperbarui UI.
     */
    private fun loadWishlistedMovies() {
        val user = auth.currentUser

        if (user == null) {
            binding.tvEmptyWishlist.text = "Login untuk melihat wishlist"
            binding.tvEmptyWishlist.visibility = View.VISIBLE
            binding.recyclerViewWishlist.visibility = View.GONE
            wishlistedMovies.clear()
            wishlistAdapter.notifyDataSetChanged()
            return
        }

        val userId = user.email!!
        Log.d("WishlistFragment", "Loading wishlist for user: $userId")
        val moviesFromDb = wishlistDbHelper.getWishlistMoviesByUser(userId)

        wishlistedMovies.clear()
        wishlistedMovies.addAll(moviesFromDb)

        if (wishlistedMovies.isEmpty()) {
            binding.tvEmptyWishlist.text = getString(R.string.empty_wishlist)
            binding.tvEmptyWishlist.visibility = View.VISIBLE
            binding.recyclerViewWishlist.visibility = View.GONE
        } else {
            binding.tvEmptyWishlist.visibility = View.GONE
            binding.recyclerViewWishlist.visibility = View.VISIBLE
        }

        wishlistAdapter.setInitialWishlist(wishlistedMovies.map { it.id }.toSet())
        wishlistAdapter.notifyDataSetChanged()
    }

    /**
     * Fungsi yang dipanggil saat ikon hati di salah satu item diklik.
     */
    private fun handleRemoveFromWishlist(movie: Movie) {
        val user = auth.currentUser ?: return
        val userId = user.email!!

        val rowsDeleted = wishlistDbHelper.removeMovieFromWishlist(movie.id, userId)
        if (rowsDeleted > 0) {
            Toast.makeText(requireContext(), "${movie.title} dihapus dari wishlist", Toast.LENGTH_SHORT).show()
            loadWishlistedMovies()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}