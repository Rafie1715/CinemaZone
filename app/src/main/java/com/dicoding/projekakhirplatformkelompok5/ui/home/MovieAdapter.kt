package com.dicoding.projekakhirplatformkelompok5.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.projekakhirplatformkelompok5.R
import com.dicoding.projekakhirplatformkelompok5.data.model.Movie
import com.dicoding.projekakhirplatformkelompok5.databinding.ItemMovieBinding

class MovieAdapter(
    private val context: Context,
    private var movieList: MutableList<Movie>,
    private val onMovieClickListener: (Movie) -> Unit,
    private val onWishlistClickListener: (Movie, Boolean) -> Unit // movie, isAddingToWishlist
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    // Simpan ID film yang ada di wishlist untuk update UI ikon
    private val wishlistedMovieIds = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int = movieList.size

    fun updateData(newMovies: List<Movie>) {
        movieList.clear()
        movieList.addAll(newMovies)
        notifyDataSetChanged()
    }

    // Panggil ini dari fragment setelah status wishlist di database/SharedPreferences diubah
    fun setWishlistStatus(movieId: Int, isWishlisted: Boolean) {
        if (isWishlisted) {
            wishlistedMovieIds.add(movieId)
        } else {
            wishlistedMovieIds.remove(movieId)
        }
        val index = movieList.indexOfFirst { it.id == movieId }
        if (index != -1) {
            notifyItemChanged(index, "wishlist_changed_payload")
        }
    }

    // Panggil ini saat adapter pertama kali dibuat untuk memuat status wishlist awal
    fun setInitialWishlist(ids: Set<Int>) {
        wishlistedMovieIds.clear()
        wishlistedMovieIds.addAll(ids)
        notifyDataSetChanged() // Atau loop dan notifyItemChanged jika lebih efisien
    }


    inner class MovieViewHolder(private val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.tvMovieTitle.text = movie.title
            binding.tvMovieReleaseDate.text = "${context.getString(R.string.release_date_label)}${movie.releaseDate ?: "N/A"}"
            binding.tvMovieRating.text = "${context.getString(R.string.rating_label)}${movie.voteAverage?.toString() ?: "N/A"}"
            binding.tvMovieOverviewSnippet.text = movie.overview

            // GANTI "YOUR_BASE_IMAGE_URL" dengan base URL gambar dari API/sumbermu
            // Contoh: "https://image.tmdb.org/t/p/w500"
            // Jika posterPath sudah URL lengkap, tidak perlu base URL.
            val posterUrl = if (movie.posterPath != null && movie.posterPath.startsWith("/")) {
                "https://image.tmdb.org/t/p/w500" + movie.posterPath
            } else {
                movie.posterPath // Asumsikan sudah URL lengkap jika tidak diawali "/"
            }

            Glide.with(context)
                .load(posterUrl)
                .placeholder(R.drawable.ic_cinema) // Ganti dengan drawable placeholder-mu
                .error(R.drawable.ic_broken_image) // Ganti dengan drawable error-mu
                .into(binding.ivMoviePoster)

            // Update ikon wishlist
            updateWishlistIcon(movie.id)

            binding.root.setOnClickListener {
                onMovieClickListener(movie)
            }

            binding.ivAddToWishlistItem.setOnClickListener {
                val isCurrentlyWishlisted = wishlistedMovieIds.contains(movie.id)
                // Panggil listener di fragment, fragment yang akan urus logika DB/SharedPreferences
                onWishlistClickListener(movie, !isCurrentlyWishlisted)
                // UI icon akan diupdate setelah fragment memanggil setWishlistStatus
            }
        }

        private fun updateWishlistIcon(movieId: Int) {
            if (wishlistedMovieIds.contains(movieId)) {
                binding.ivAddToWishlistItem.setImageResource(R.drawable.ic_favorite_filled) // Ikon terisi
                binding.ivAddToWishlistItem.contentDescription = context.getString(R.string.remove_from_wishlist)
            } else {
                binding.ivAddToWishlistItem.setImageResource(R.drawable.ic_favorite) // Ikon border
                binding.ivAddToWishlistItem.contentDescription = context.getString(R.string.add_to_wishlist)
            }
        }
    }

    // Untuk update spesifik pada icon wishlist tanpa rebind seluruh item
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains("wishlist_changed_payload")) {
            val movie = movieList[position]
            holder.bind(movie) // Atau cukup panggil holder.updateWishlistIcon(movie.id)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }
}