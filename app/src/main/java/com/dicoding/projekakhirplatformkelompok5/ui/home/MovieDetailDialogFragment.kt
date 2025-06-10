package com.dicoding.projekakhirplatformkelompok5.ui.home

import android.content.Intent
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
import androidx.core.net.toUri

class MovieDetailDialogFragment : DialogFragment() {

    private var _binding: DialogMovieDetailBinding? = null
    private val binding get() = _binding!!

    private var movie: Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movie = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_MOVIE, Movie::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARG_MOVIE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // Atur ukuran dialog
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        movie?.let { populateUi(it) }
    }

    private fun populateUi(movie: Movie) {
        binding.tvDetailTitle.text = movie.title
        binding.tvDetailOverview.text = movie.overview

        Glide.with(this)
            .load(movie.posterPath)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(binding.ivDetailPoster)

        binding.btnWatchTrailer.setOnClickListener {
            val trailerUrl = movie.trailerUrl
            if (!trailerUrl.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, trailerUrl.toUri())
                try {
                    startActivity(intent)
                } catch (_: Exception) {
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
