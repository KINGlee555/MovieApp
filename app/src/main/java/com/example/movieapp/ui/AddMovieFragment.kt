package com.example.movieapp.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.movieapp.R
import com.example.movieapp.data.models.Movie
import com.example.movieapp.databinding.FragmentAddMovieBinding
import com.example.movieapp.databinding.FragmentAllMoviesBinding
import com.example.movieapp.ui.viewmodel.MovieViewModel
import com.example.movieapp.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddMovieFragment : Fragment(R.layout.fragment_add_movie) {

    private val viewModel: MovieViewModel by activityViewModels()
    private var binding: FragmentAddMovieBinding by autoCleared()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddMovieBinding.bind(view)

        binding.btnSave.setOnClickListener {
            val title = binding.Title.text.toString().trim()
            val desc = binding.Desc.text.toString().trim()

            if (title.isNotEmpty() && desc.isNotEmpty()) {
                val newMovie = Movie(
                    title = title,
                    posterPath = null, // בהוספה ידנית אין לנו תמונה מה-API
                    overview = desc,
                    rating = 0.0,
                    isFavorite = binding.Favoritebtn.isChecked,
                    isWatched = binding.Watchedbtn.isChecked
                )

                viewModel.updateMovieStatus(newMovie)
                Toast.makeText(requireContext(), "הסרט נשמר בהצלחה", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "נא למלא את כל השדות", Toast.LENGTH_SHORT).show()
            }
        }
    }

}