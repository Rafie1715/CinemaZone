package com.dicoding.projekakhirplatformkelompok5.ui.wishlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.projekakhirplatformkelompok5.R
import com.dicoding.projekakhirplatformkelompok5.data.local.WishlistDatabaseHelper
import com.dicoding.projekakhirplatformkelompok5.data.model.Movie
import com.dicoding.projekakhirplatformkelompok5.databinding.FragmentWishlistBinding
import com.dicoding.projekakhirplatformkelompok5.ui.home.MovieAdapter
import com.dicoding.projekakhirplatformkelompok5.ui.home.MovieDetailDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class WishlistFragment : Fragment() {

    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var wishlistDbHelper: WishlistDatabaseHelper
    private lateinit var wishlistAdapter: MovieAdapter
    private val wishlistedMovies = mutableListOf<Movie>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        wishlistDbHelper = WishlistDatabaseHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        loadWishlistedMovies()
    }

    private fun setupRecyclerView() {
        wishlistAdapter = MovieAdapter(
            requireContext(),
            wishlistedMovies,
            onMovieClickListener = { movie ->
                MovieDetailDialogFragment.newInstance(movie).show(parentFragmentManager, MovieDetailDialogFragment.TAG)
            },
            onWishlistClickListener = { movie, shouldBeWishlisted ->
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

    private fun loadWishlistedMovies() {
        val user = auth.currentUser
        if (user == null) {
            binding.tvEmptyWishlist.text = "Login untuk melihat wishlist"
            updateUiVisibility(isDataEmpty = true)
            return
        }

        val moviesFromDb = wishlistDbHelper.getWishlistMoviesByUser(user.uid)

        wishlistedMovies.clear()
        wishlistedMovies.addAll(moviesFromDb)

        updateUiVisibility(wishlistedMovies.isEmpty())

        wishlistAdapter.setInitialWishlist(wishlistedMovies.map { it.id }.toSet())
        wishlistAdapter.notifyDataSetChanged()
    }

    private fun updateUiVisibility(isDataEmpty: Boolean) {
        if (isDataEmpty) {
            binding.tvEmptyWishlist.text = getString(R.string.empty_wishlist)
            binding.tvEmptyWishlist.visibility = View.VISIBLE
            binding.recyclerViewWishlist.visibility = View.GONE
        } else {
            binding.tvEmptyWishlist.visibility = View.GONE
            binding.recyclerViewWishlist.visibility = View.VISIBLE
        }
    }

    private fun handleRemoveFromWishlist(movie: Movie) {
        val user = auth.currentUser ?: return

        wishlistDbHelper.removeMovieFromWishlist(movie.id, user.uid)
        Toast.makeText(requireContext(), "${movie.title} dihapus dari wishlist", Toast.LENGTH_SHORT).show()

        loadWishlistedMovies()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}