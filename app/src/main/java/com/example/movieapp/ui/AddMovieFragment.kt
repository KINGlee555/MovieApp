package com.example.movieapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.data.models.Movie
import com.example.movieapp.databinding.FragmentAddMovieBinding
import com.example.movieapp.ui.viewmodel.MovieViewModel
import com.example.movieapp.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddMovieFragment : Fragment() {

    private val viewModel: MovieViewModel by activityViewModels()
    private var binding: FragmentAddMovieBinding by autoCleared()

    private var selectedImageUri: String? = null

    // 2. הגדרת ה-Launcher
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                selectedImageUri = uri.toString()
                Glide.with(this).load(uri).fitCenter().into(binding.imgPosterPreview)
            }
        }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddMovieBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddMovieBinding.bind(view)

        binding.imgPosterPreview.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnSave.setOnClickListener {
            val title = binding.Title.text.toString().trim()
            val desc = binding.Desc.text.toString().trim()

            if (title.isNotEmpty() && desc.isNotEmpty() && selectedImageUri != null) {
                val uniqueId = System.currentTimeMillis().toInt()
                val newMovie = Movie(
                    id = if (uniqueId < 0) -uniqueId else uniqueId,
                    title = title,
                    posterPath = selectedImageUri,
                    overview = desc,
                    rating = binding.movieRating.rating.toDouble(),
                    isFavorite = binding.Favoritebtn.isChecked,
                    isWatched = binding.Watchedbtn.isChecked,
                    isInWatchList = binding.WatchListbtn.isChecked,
                    isManualEntry = true
                )

                viewModel.addMovie(newMovie)
                Toast.makeText(requireContext(), getString(R.string.movie_saved_successfully), Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else if (title.isEmpty() || desc.isEmpty()) {
                // Localization fix: Error message for empty fields
                Toast.makeText(requireContext(), getString(R.string.please_fill_in_all_fields), Toast.LENGTH_SHORT).show()
            } else if (selectedImageUri == null) {
                // Localization fix: Error message for missing image
                Toast.makeText(requireContext(), getString(R.string.please_select_a_poster), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
