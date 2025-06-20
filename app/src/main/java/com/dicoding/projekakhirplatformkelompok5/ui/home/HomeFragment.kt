package com.dicoding.projekakhirplatformkelompok5.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.projekakhirplatformkelompok5.data.model.Movie
import com.dicoding.projekakhirplatformkelompok5.data.network.ApiClient
import com.dicoding.projekakhirplatformkelompok5.databinding.FragmentHomeBinding
import com.google.android.material.chip.Chip
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val db = Firebase.firestore
    private val fullMovieList = mutableListOf<Movie>()
    private val displayedMovieList = mutableListOf<Movie>()
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var auth: FirebaseAuth

    private val wishlistedMovieIds = mutableSetOf<Int>()

    private var currentQuery: String = ""
    private var currentGenre: String = "Semua"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchListener()
        fetchUserWishlist()
        fetchMovies()
    }

    private fun fetchUserWishlist() {
        val user = auth.currentUser ?: return
        db.collection("users").document(user.uid).collection("wishlist").get()
            .addOnSuccessListener { result ->
                if (_binding == null) return@addOnSuccessListener
                wishlistedMovieIds.clear()
                for (document in result) {
                    document.id.toIntOrNull()?.let { wishlistedMovieIds.add(it) }
                }
                movieAdapter.setInitialWishlist(wishlistedMovieIds)
            }
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(
            requireContext(),
            displayedMovieList,
            onMovieClickListener = { movie ->
                MovieDetailDialogFragment.newInstance(movie).show(parentFragmentManager, MovieDetailDialogFragment.TAG)
            },
            onWishlistClickListener = { movie, addToWishlist ->
                handleWishlistAction(movie, addToWishlist)
            }
        )
        binding.recyclerViewMovies.adapter = movieAdapter
        binding.recyclerViewMovies.layoutManager = LinearLayoutManager(context)
    }

    private fun setupSearchListener() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText.orEmpty()
                filterMovies()
                return true
            }
        })
    }

    private fun setupGenreChips() {
        val genres = fullMovieList.flatMap { it.genre ?: emptyList() }.distinct().sorted()
        binding.chipGroupGenre.removeAllViews()

        fun createChip(genreName: String): Chip {
            return Chip(context).apply {
                text = genreName
                isCheckable = true
                isClickable = true
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        currentGenre = genreName
                        filterMovies()
                    }
                }
            }
        }

        val allChip = createChip("Semua").apply { isChecked = true }
        binding.chipGroupGenre.addView(allChip)
        genres.forEach { genre ->
            binding.chipGroupGenre.addView(createChip(genre))
        }
        binding.chipGroupGenre.isSingleSelection = true
    }

    private fun filterMovies() {
        var filteredList: List<Movie> = fullMovieList
        if (currentGenre != "Semua") {
            filteredList = filteredList.filter { it.genre?.contains(currentGenre) == true }
        }
        if (currentQuery.isNotEmpty()) {
            filteredList = filteredList.filter { it.title.contains(currentQuery, ignoreCase = true) }
        }

        displayedMovieList.clear()
        displayedMovieList.addAll(filteredList)
        movieAdapter.notifyDataSetChanged()

        if (displayedMovieList.isEmpty()) {
            binding.recyclerViewMovies.visibility = View.GONE
            binding.tvErrorHome.text = "Film tidak ditemukan."
            binding.tvErrorHome.visibility = View.VISIBLE
        } else {
            binding.recyclerViewMovies.visibility = View.VISIBLE
            binding.tvErrorHome.visibility = View.GONE
        }
    }

    private fun fetchMovies() {
        binding.progressBarHome.visibility = View.VISIBLE
        binding.tvErrorHome.visibility = View.GONE
        binding.recyclerViewMovies.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.instance.getAllMovies()
                if (response.isSuccessful) {
                    response.body()?.let { movies ->
                        if (_binding != null) {
                            fullMovieList.clear()
                            fullMovieList.addAll(movies)
                            setupGenreChips()
                            filterMovies()
                        }
                    }
                } else {
                    if (_binding != null) {
                        binding.tvErrorHome.text = "Gagal memuat: Error ${response.code()}"
                        binding.tvErrorHome.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                if (_binding != null && e !is kotlinx.coroutines.CancellationException) {
                    binding.tvErrorHome.text = "Gagal terhubung. Periksa koneksi internet Anda."
                    binding.tvErrorHome.visibility = View.VISIBLE
                    Log.e("HomeFragment", "Exception: ${e.message}", e)
                }
            } finally {
                if (_binding != null) {
                    binding.progressBarHome.visibility = View.GONE
                }
            }
        }
    }

    private fun handleWishlistAction(movie: Movie, addToWishlist: Boolean) {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(requireContext(), "Silakan login untuk menggunakan wishlist", Toast.LENGTH_SHORT).show()
            return
        }

        val wishlistItemRef = db.collection("users").document(user.uid)
            .collection("wishlist").document(movie.id.toString())

        if (addToWishlist) {
            val wishlistItem = hashMapOf(
                "title" to movie.title,
                "posterPath" to movie.posterPath,
                "addedDate" to System.currentTimeMillis()
            )
            wishlistItemRef.set(wishlistItem)
                .addOnSuccessListener { Toast.makeText(context, "${movie.title} ditambahkan ke wishlist", Toast.LENGTH_SHORT).show() }
        } else {
            wishlistItemRef.delete()
                .addOnSuccessListener { Toast.makeText(context, "${movie.title} dihapus dari wishlist", Toast.LENGTH_SHORT).show() }
        }
        movieAdapter.setWishlistStatus(movie.id, addToWishlist)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}