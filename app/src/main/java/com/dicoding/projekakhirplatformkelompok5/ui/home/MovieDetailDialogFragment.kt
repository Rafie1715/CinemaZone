package com.dicoding.projekakhirplatformkelompok5.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.dicoding.projekakhirplatformkelompok5.R
import com.dicoding.projekakhirplatformkelompok5.data.model.Movie
import com.dicoding.projekakhirplatformkelompok5.databinding.DialogMovieDetailBinding

class MovieDetailDialogFragment : DialogFragment() {

    private var _binding: DialogMovieDetailBinding? = null
    private val binding get() = _binding!!

    // Properti untuk menampung data movie
    private var movie: Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ambil objek Movie dari arguments
        movie = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_MOVIE, Movie::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARG_MOVIE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        movie?.let { populateUi(it) }
    }

    private fun populateUi(movie: Movie) {
        binding.tvDetailTitle.text = movie.title
        binding.tvDetailOverview.text = movie.overview

        Glide.with(this)
            .load(movie.posterPath)
            .placeholder(R.drawable.ic_cinema)
            .error(R.drawable.ic_broken_image)
            .into(binding.ivDetailPoster)

        // Set listener untuk tombol trailer
        binding.btnWatchTrailer.setOnClickListener {
            val trailerUrl = movie.trailerUrl
            if (!trailerUrl.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl))
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Tidak dapat membuka link trailer.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Link trailer tidak tersedia.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "MovieDetailDialog"
        private const val ARG_MOVIE = "movie_arg"

        // Fungsi factory untuk membuat instance DialogFragment dengan data Movie
        fun newInstance(movie: Movie): MovieDetailDialogFragment {
            val args = Bundle().apply {
                putParcelable(ARG_MOVIE, movie)
            }
            return MovieDetailDialogFragment().apply {
                arguments = args
            }
        }
    }
}
