package com.example.movieapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.movieapp.R
import com.example.movieapp.data.Movie
import com.example.movieapp.databinding.FragmentAddMovieBinding
import com.example.movieapp.viewmodel.MovieViewModel

class AddMovieFragment : Fragment() {

    // Sharing the ViewModel with Activity scope
    private val viewModel: MovieViewModel by activityViewModels()

    private var _binding: FragmentAddMovieBinding? = null
    private val binding get() = _binding!!

    // Variable to store the selected image URI
    private var selectedImageUri: Uri? = null

    // Registering the gallery picker launcher
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.movieImageResult.setImageURI(it) // Show preview in UI

            // Persist permission to access this URI after app restarts
            requireActivity().contentResolver.takePersistableUriPermission(
                it, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Launch gallery to pick image
        binding.btnSelectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Save movie button logic
        binding.btnSaveMovie.setOnClickListener {
            val title = binding.etMovieTitle.text.toString().trim()
            val description = binding.etMovieDescription.text.toString().trim()
            val rating = binding.ratingBarInput.rating

            if (title.isNotEmpty() && description.isNotEmpty()) {
                // Create Movie object
                val movie = Movie(
                    title = title,
                    description = description,
                    imageUri = selectedImageUri?.toString(),
                    rating = rating // Passing the rating here
                )

                // Add to DB using ViewModel
                viewModel.addMovie(movie)

                Toast.makeText(requireContext(),
                    getString(R.string.movie_added_toast, rating), Toast.LENGTH_SHORT).show()
                findNavController().popBackStack() // Go back to list
            } else {
                Toast.makeText(requireContext(),
                    getString(R.string.fill_all_fields_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}