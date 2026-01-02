package com.example.movieapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.data.Movie
import com.example.movieapp.databinding.MovieLayoutBinding

class MovieAdapter(
    private var movies: List<Movie>,
    private val callback: MovieListener
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    // Interface to handle clicks on items
    interface MovieListener {
        fun onMovieClicked(movie: Movie)
        fun onMovieLongClicked(movie: Movie)
    }

    // ViewHolder holds the references to the views for each row
    inner class MovieViewHolder(private val binding: MovieLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.movieTitle.text = movie.title
            binding.movieDescription.text = movie.description
            binding.movieRating.rating = movie.rating
            // Loading the image using Glide from the URI stored in Room
            Glide.with(binding.root.context)
                .load(movie.imageUri)
                .centerCrop()
                // Placeholder image if the movie has no poster
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(binding.movieImage)

            // Setup click listeners
            binding.root.setOnClickListener {
                callback.onMovieClicked(movie)
            }

            binding.root.setOnLongClickListener {
                callback.onMovieLongClicked(movie)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = MovieLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size

    // Helper function to update the list and refresh the UI
    fun setMovies(newMovies: List<Movie>) {
        this.movies = newMovies
        notifyDataSetChanged()
    }

    // Helper to get a movie at a specific position (useful for swipe to delete)
    fun getMovieAt(position: Int): Movie = movies[position]
}