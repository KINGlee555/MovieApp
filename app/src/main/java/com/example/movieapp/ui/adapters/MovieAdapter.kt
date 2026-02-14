package com.example.movieapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.data.models.Movie
import com.example.movieapp.databinding.MovieLayoutBinding
import com.example.movieapp.utils.Constants

class MovieAdapter(private val listener: OnMovieClickListener) :
    ListAdapter<Movie, MovieAdapter.MovieViewHolder>(MovieDiffCallback()) {

    interface OnMovieClickListener {
        fun onMovieClick(id: Int)
        fun onMovieLongClick(movie: Movie) // הוספת פונקציה ללחיצה ארוכה המקבלת את כל האובייקט
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = MovieLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MovieViewHolder(private val binding: MovieLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.movieTitle.text = movie.title
            binding.movieDescription.text = movie.overview
            binding.movieRating.rating = movie.rating.toFloat() / 2 // המרה לדירוג של 5 כוכבים

            // שימוש ב-Glide לטעינת התמונה מ-TMDB
            Glide.with(binding.root.context)
                .load(movie.getFullPosterPath())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(binding.movieImage)

            binding.root.setOnClickListener {
                listener.onMovieClick(id = movie.id)
            }
            binding.root.setOnLongClickListener {
                listener.onMovieLongClick(movie)
                true // החזרת true כדי לציין שהאירוע טופל
            }
        }
    }

    // מחלקה לחישוב הבדלים בין רשימות - הופך את הרענון למהיר מאוד
    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Movie, newItem: Movie) = oldItem == newItem
    }
}