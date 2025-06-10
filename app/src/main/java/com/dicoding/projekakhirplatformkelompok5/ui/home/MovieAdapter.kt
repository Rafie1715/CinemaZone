package com.dicoding.projekakhirplatformkelompok5.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.projekakhirplatformkelompok5.R
import com.dicoding.projekakhirplatformkelompok5.data.model.Movie
import com.dicoding.projekakhirplatformkelompok5.databinding.ItemMovieBinding

class MovieAdapter(
    private val context: Context,
    private var movieList: List<Movie>,
    private val onMovieClickListener: (Movie) -> Unit,
    private val onWishlistClickListener: (Movie, Boolean) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private val wishlistedMovieIds = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movieList[position])
        holder.itemView.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.item_slide_up_fade_in)
    }

    override fun getItemCount(): Int = movieList.size

    fun setInitialWishlist(ids: Set<Int>) {
        wishlistedMovieIds.clear()
        wishlistedMovieIds.addAll(ids)
        notifyDataSetChanged()
    }

    fun setWishlistStatus(movieId: Int, isWishlisted: Boolean) {
        if (isWishlisted) {
            wishlistedMovieIds.add(movieId)
        } else {
            wishlistedMovieIds.remove(movieId)
        }
        val index = movieList.indexOfFirst { it.id == movieId }
        if (index != -1) {
            notifyItemChanged(index)
        }
    }

    inner class MovieViewHolder(private val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.tvMovieTitle.text = movie.title
            binding.tvMovieReleaseDate.text = "Release Date: ${movie.releaseDate ?: "N/A"}"
            binding.tvMovieRating.text = movie.voteAverage.toString()
            binding.tvMovieOverviewSnippet.text = movie.overview

            Glide.with(context)
                .load(movie.posterPath)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(binding.ivMoviePoster)

            val isWishlisted = wishlistedMovieIds.contains(movie.id)
            if (isWishlisted) {
                binding.ivAddToWishlistItem.setImageResource(R.drawable.ic_favorite_filled)
            } else {
                binding.ivAddToWishlistItem.setImageResource(R.drawable.ic_favorite)
            }

            binding.root.setOnClickListener {
                onMovieClickListener(movie)
            }

            binding.ivAddToWishlistItem.setOnClickListener { view ->
                view.animate()
                    .scaleX(1.3f).scaleY(1.3f)
                    .setDuration(150)
                    .withEndAction {
                        view.animate()
                            .scaleX(1f).scaleY(1f)
                            .setDuration(150)
                            .start()
                    }.start()

                val currentlyWishlisted = wishlistedMovieIds.contains(movie.id)
                onWishlistClickListener(movie, !currentlyWishlisted)
            }
        }
    }
}