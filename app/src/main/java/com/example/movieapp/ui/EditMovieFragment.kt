package com.example.movieapp.ui

import android.os.Build
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

    // הגדרת הלנצ'ר (Launcher) לפתיחת הגלריה
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
            Glide.with(this)
                .load(currentMovie.getFullPosterPath())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(binding.imgPosterPreview)
            binding.imgPosterPreview.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            binding.btnUpdateMovie.setOnClickListener {
                val title = binding.EditMovieTitle.text.toString().trim()
                val overview = binding.EditMovieDescription.text.toString().trim()

                if (title.isNotEmpty()) {
                    val updatedMovie = currentMovie.copy(
                        title = title,
                        overview = overview,
                        posterPath = selectedImageUri // שימוש בכתובת השמורה (URI) ולא ב-View
                    )
                viewModel.updateMovieStatus(updatedMovie)
                Toast.makeText(requireContext(), "Movie Updated", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }
    }
}