package com.dicoding.projekakhirplatformkelompok5.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.projekakhirplatformkelompok5.R
import com.dicoding.projekakhirplatformkelompok5.data.local.WishlistDatabaseHelper
import com.dicoding.projekakhirplatformkelompok5.data.model.Movie
import com.dicoding.projekakhirplatformkelompok5.data.network.ApiClient
import com.dicoding.projekakhirplatformkelompok5.databinding.FragmentHomeBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    // Gunakan _binding yang nullable
    private var _binding: FragmentHomeBinding? = null
    // Properti 'binding' hanya boleh diakses antara onCreateView dan onDestroyView.
    private val binding get() = _binding!!

    private lateinit var movieAdapter: MovieAdapter
    private val movieList = mutableListOf<Movie>()
    private lateinit var wishlistDbHelper: WishlistDatabaseHelper

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        wishlistDbHelper = WishlistDatabaseHelper(requireContext())
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchMovies()
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(
            requireContext(),
            movieList,
            onMovieClickListener = { movie ->
                // === UBAH BAGIAN INI ===
                // Bukan lagi menampilkan Toast, tapi menampilkan DialogFragment
                MovieDetailDialogFragment.newInstance(movie)
                    .show(parentFragmentManager, MovieDetailDialogFragment.TAG)
            },
            onWishlistClickListener = { movie, addToWishlist ->
                handleWishlistAction(movie, addToWishlist)
            }
        )
        binding.recyclerViewMovies.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = movieAdapter
        }
    }

    private fun fetchMovies() {
        binding.progressBarHome.visibility = View.VISIBLE
        binding.tvErrorHome.visibility = View.GONE
        binding.recyclerViewMovies.visibility = View.GONE

        // Gunakan viewLifecycleOwner.lifecycleScope untuk Coroutine yang sadar lifecycle View
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.instance.getAllMoviesDirect()
                if (response.isSuccessful) {
                    response.body()?.let { movies ->
                        // Pastikan view masih ada sebelum update UI
                        if (_binding != null) {
                            movieList.clear()
                            movieList.addAll(movies)
                            movieAdapter.notifyDataSetChanged()
                            loadInitialWishlistStatus() // Panggil setelah movie list terisi
                            binding.recyclerViewMovies.visibility = View.VISIBLE
                        }
                    } ?: run {
                        if (_binding != null) {
                            binding.tvErrorHome.text = getString(R.string.no_movies_found)
                            binding.tvErrorHome.visibility = View.VISIBLE
                        }
                    }
                } else {
                    Log.e("HomeFragment", "Error: ${response.code()} - ${response.message()}")
                    if (_binding != null) {
                        binding.tvErrorHome.text = "${getString(R.string.failed_to_load_movies)} (Error: ${response.code()})"
                        binding.tvErrorHome.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                // JobCancellationException akan masuk ke sini jika coroutine dibatalkan
                // Kita tidak perlu menganggapnya sebagai error fatal
                if (e is kotlinx.coroutines.CancellationException) {
                    Log.i("HomeFragment", "Fetch movies job was cancelled.")
                } else {
                    Log.e("HomeFragment", "Exception: ${e.message}", e)
                    if (_binding != null) {
                        binding.tvErrorHome.text = "${getString(R.string.failed_to_load_movies)} (Network Error)"
                        binding.tvErrorHome.visibility = View.VISIBLE
                    }
                }
            } finally {
                // Lakukan pengecekan null di sini juga, ini yang paling penting
                if (_binding != null) {
                    binding.progressBarHome.visibility = View.GONE
                }
            }
        }
    }

    private fun loadInitialWishlistStatus() {
        val user = auth.currentUser
        val wishlistedIds = mutableSetOf<Int>()

        if (user != null) {
            val userId = user.email!! // Asumsi email selalu ada jika sudah login
            movieList.forEach { movie ->
                if (wishlistDbHelper.isMovieInWishlist(movie.id, userId)) {
                    wishlistedIds.add(movie.id)
                }
            }
        }
        movieAdapter.setInitialWishlist(wishlistedIds)
    }

    private fun handleWishlistAction(movie: Movie, addToWishlist: Boolean) {
        val user = auth.currentUser

        if (user == null) {
            Toast.makeText(requireContext(), "Silakan login untuk menggunakan wishlist", Toast.LENGTH_SHORT).show()
            return
        }
        val userId = user.email!!

        if (addToWishlist) {
            wishlistDbHelper.addMovieToWishlist(movie, userId)
            Toast.makeText(requireContext(), "${movie.title} ditambahkan ke wishlist", Toast.LENGTH_SHORT).show()
        } else {
            wishlistDbHelper.removeMovieFromWishlist(movie.id, userId)
            Toast.makeText(requireContext(), "${movie.title} dihapus dari wishlist", Toast.LENGTH_SHORT).show()
        }
        movieAdapter.setWishlistStatus(movie.id, addToWishlist)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // _binding di-set null di sini, ini adalah praktik yang benar.
        _binding = null
    }
}