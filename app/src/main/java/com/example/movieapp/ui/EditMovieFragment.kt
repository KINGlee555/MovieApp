package com.example.movieapp.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.movieapp.data.models.Movie
import com.example.movieapp.databinding.FragmentEditMovieBinding
import com.example.movieapp.ui.viewmodel.MovieViewModel
import com.example.movieapp.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditMovieFragment : Fragment() {

    private var binding: FragmentEditMovieBinding by autoCleared()
    private val viewModel: MovieViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

            binding.btnUpdateMovie.setOnClickListener {
                val updatedMovie = currentMovie.copy(
                    title = binding.EditMovieTitle.text.toString(),
                    overview = binding.EditMovieDescription.text.toString()
                )
                viewModel.updateMovieStatus(updatedMovie)
                Toast.makeText(requireContext(), "Movie Updated", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }
}