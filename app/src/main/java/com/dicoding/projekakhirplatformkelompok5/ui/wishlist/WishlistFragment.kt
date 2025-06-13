package com.dicoding.projekakhirplatformkelompok5.ui.wishlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.projekakhirplatformkelompok5.R
import com.dicoding.projekakhirplatformkelompok5.data.model.Movie
import com.dicoding.projekakhirplatformkelompok5.data.network.ApiClient
import com.dicoding.projekakhirplatformkelompok5.databinding.FragmentWishlistBinding
import com.dicoding.projekakhirplatformkelompok5.ui.home.MovieAdapter
import com.dicoding.projekakhirplatformkelompok5.ui.home.MovieDetailDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class WishlistFragment : Fragment() {

    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var wishlistAdapter: MovieAdapter
    private val wishlistedMovies = mutableListOf<Movie>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        auth = Firebase.auth
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
        binding.recyclerViewWishlist.adapter = wishlistAdapter
        binding.recyclerViewWishlist.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadWishlistedMovies() {
        val user = auth.currentUser
        if (user == null) {
            updateUiAfterLoad(true, "Login untuk melihat wishlist")
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val wishlistIds = getWishlistIdsFromFirestore(user.uid)
                if (_binding == null) return@launch

                if (wishlistIds.isEmpty()) {
                    updateUiAfterLoad(isEmpty = true)
                    return@launch
                }

                val response = ApiClient.instance.getAllMovies()
                if (_binding == null) return@launch

                if (response.isSuccessful) {
                    val allMovies = response.body() ?: emptyList()
                    val filteredList = allMovies.filter { movie -> movie.id in wishlistIds }

                    wishlistedMovies.clear()
                    wishlistedMovies.addAll(filteredList)
                    updateUiAfterLoad(isEmpty = wishlistedMovies.isEmpty())

                } else {
                    Toast.makeText(requireContext(), "Gagal memuat detail film wishlist", Toast.LENGTH_SHORT).show()
                    updateUiAfterLoad(isEmpty = true)
                }
            } catch (e: Exception) {
                if (_binding != null && e !is kotlinx.coroutines.CancellationException) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    updateUiAfterLoad(isEmpty = true)
                }
            }
        }
    }

    private suspend fun getWishlistIdsFromFirestore(userId: String): Set<Int> = suspendCoroutine { continuation ->
        db.collection("users").document(userId).collection("wishlist")
            .get()
            .addOnSuccessListener { result ->
                val idSet = result.documents.mapNotNull { it.id.toIntOrNull() }.toSet()
                continuation.resume(idSet)
            }
            .addOnFailureListener {
                continuation.resume(emptySet())
            }
    }

    private fun updateUiAfterLoad(isEmpty: Boolean, message: String? = null) {
        if (_binding == null) return

        if (isEmpty) {
            binding.tvEmptyWishlist.text = message ?: getString(R.string.empty_wishlist)
            binding.tvEmptyWishlist.visibility = View.VISIBLE
            binding.recyclerViewWishlist.visibility = View.GONE
        } else {
            binding.tvEmptyWishlist.visibility = View.GONE
            binding.recyclerViewWishlist.visibility = View.VISIBLE
        }
        wishlistAdapter.setInitialWishlist(wishlistedMovies.map { it.id }.toSet())
        wishlistAdapter.notifyDataSetChanged()
    }

    private fun handleRemoveFromWishlist(movie: Movie) {
        val user = auth.currentUser ?: return
        db.collection("users").document(user.uid).collection("wishlist")
            .document(movie.id.toString()).delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "${movie.title} dihapus dari wishlist", Toast.LENGTH_SHORT).show()
                loadWishlistedMovies()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}