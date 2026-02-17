package com.example.movieapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.data.models.Movie
import com.example.movieapp.databinding.FragmentEditMovieBinding
import com.example.movieapp.ui.viewmodel.MovieViewModel
import com.example.movieapp.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditMovieFragment : Fragment() {

    private var binding: FragmentEditMovieBinding by autoCleared()
    private val viewModel: MovieViewModel by viewModels()

    private var selectedImageUri: String? = null

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedImageUri = uri.toString()
            Glide.with(this).load(uri).into(binding.imgPosterPreview)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movie = arguments?.let {
            BundleCompat.getParcelable(it, "movie", Movie::class.java)
        }
        movie?.let { currentMovie ->
            binding.EditMovieTitle.setText(currentMovie.title)
            binding.EditMovieDescription.setText(currentMovie.overview)
            selectedImageUri = currentMovie.posterPath
            binding.movieRating.rating = currentMovie.rating.toFloat()
            binding.WatchListbtn.isChecked = currentMovie.isInWatchList
            Glide.with(this)
                .load(currentMovie.getFullPosterPath())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .fitCenter()
                .into(binding.imgPosterPreview)
            binding.imgPosterPreview.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            binding.btnUpdateMovie.setOnClickListener {
                val title = binding.EditMovieTitle.text.toString().trim()
                val overview = binding.EditMovieDescription.text.toString().trim()
                val isInWatchList = binding.WatchListbtn.isChecked
                val rating = binding.movieRating.rating.toDouble()

                if (title.isNotEmpty() && overview.isNotEmpty() && selectedImageUri != null) {
                    val updatedMovie = currentMovie.copy(
                        title = title,
                        overview = overview,
                        posterPath = selectedImageUri,
                        isInWatchList = isInWatchList,
                        rating = rating,
                        isFavorite = binding.Favoritebtn.isChecked,
                        isWatched = binding.Watchedbtn.isChecked,
                        isManualEntry = true
                    )
                viewModel.updateMovieStatus(updatedMovie)
                Toast.makeText(requireContext(), getString(R.string.movie_updated), Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
                else if (title.isEmpty() || overview.isEmpty()) {
                    Toast.makeText(requireContext(), getString(R.string.please_fill_in_all_fields), Toast.LENGTH_SHORT).show()
                } else if (selectedImageUri == null) {
                    Toast.makeText(requireContext(), getString(R.string.please_select_a_poster), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
