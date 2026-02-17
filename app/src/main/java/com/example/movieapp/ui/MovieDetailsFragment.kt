package com.example.movieapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.movieapp.R
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.movieapp.data.models.Movie
import com.example.movieapp.databinding.FragmentMovieDetailsBinding
import com.example.movieapp.ui.viewmodel.MovieViewModel
import com.example.movieapp.utils.Loading
import com.example.movieapp.utils.Success
import com.example.movieapp.utils.Error
import com.example.movieapp.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {


    private var binding: FragmentMovieDetailsBinding by autoCleared()

    private val viewModel: MovieViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.movie.observe(viewLifecycleOwner) { resource ->
            when(resource.status) {
                is Loading -> {
                    binding.progressBar.isVisible = true
                }
                is Success -> {
                    binding.progressBar.isVisible = false
                    resource.status.data?.let { movie ->
                        updateUI(movie)
                    }
                }
                is Error -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(), resource.status.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Logic for Toggle Buttons
        binding.btnToggleFavorite.setOnClickListener {
            viewModel.toggleFavorite()
        }

        binding.btnToggleWatched.setOnClickListener {
            viewModel.toggleWatched()
        }
        binding.btnToggleWatchList.setOnClickListener {
            viewModel.toggleWatchList()
        }

        binding.btnShareMovie.setOnClickListener {
            val movieName = binding.movieTitle.text.toString()
            val bundle = Bundle().apply {
                putString("movieTitle", movieName)
            }
            findNavController().navigate(R.id.action_movieDetailsFragment_to_shareMovieFragment, bundle)
        }


        // Get ID from arguments and notify ViewModel
        arguments?.getInt("id")?.let { id ->
            viewModel.setMovieId(id)
        }
    }

    private fun updateUI(movie: Movie) {
        binding.movieTitle.text = movie.title
        binding.movieDesc.text = movie.overview
        binding.movieRating.rating = movie.rating.toFloat()

        updateButtonsUI(movie.isFavorite, movie.isWatched,movie.isInWatchList)

        Glide.with(requireContext())
            .load(movie.getFullPosterPath())
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.stat_notify_error)
            .fitCenter()
            .into(binding.movieImage)
    }

    private fun updateButtonsUI(isFavorite: Boolean, isWatched: Boolean, isInWatchList: Boolean) {
        // Update Favorite Button text
        binding.btnToggleFavorite.text = if (isFavorite) getString(R.string.remove_from_favorites) else getString(R.string.add_to_favorites)
        // Update Watched Button text
        binding.btnToggleWatched.text = if (isWatched) getString(R.string.mark_as_unwatched) else getString(R.string.mark_as_watched)
        binding.btnToggleWatchList.text = if (isInWatchList) getString(R.string.remove_from_watch_list) else getString(
            R.string.add_to_watch_list
        )

    }
}