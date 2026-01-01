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
import com.example.movieapp.databinding.FragmentEditMovieBinding
import com.example.movieapp.viewmodel.MovieViewModel
import androidx.core.net.toUri

class EditMovieFragment : Fragment() {

    // Using activityViewModels to access the shared data
    private val viewModel: MovieViewModel by activityViewModels()

    private var _binding: FragmentEditMovieBinding? = null
    private val binding get() = _binding!!

    // Variable to hold the updated image URI (defaults to the current one)
    private var updatedImageUri: String? = null

    // Launcher for picking a new image if the user wants to change it
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            updatedImageUri = it.toString()
            binding.etMovieImageResult.setImageURI(it)

            // Critical: Persist permission for the new image
            requireActivity().contentResolver.takePersistableUriPermission(
                it, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //  Pre-fill the fields with existing movie data from ViewModel
        viewModel.chosenMovie.observe(viewLifecycleOwner) { movie ->
            movie?.let {
                binding.etEditMovieTitle.setText(it.title)
                binding.etEditMovieDescription.setText(it.description)
                updatedImageUri = it.imageUri
                binding.etRatingBarInput.rating = it.rating

                if (it.imageUri != null) {
                    binding.etMovieImageResult.setImageURI(it.imageUri.toUri())
                }
            }
        }

        // 2. Option to change the image
        binding.btnChangeImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // 3. Save the updated movie
        binding.btnUpdateMovie.setOnClickListener {
            val title = binding.etEditMovieTitle.text.toString().trim()
            val description = binding.etEditMovieDescription.text.toString().trim()
            val newRating = binding.etRatingBarInput.rating
            if (title.isNotEmpty() && description.isNotEmpty()) {
                val currentMovie = viewModel.chosenMovie.value

                if (currentMovie != null) {
                    // Create a copy with updated values but KEEP THE SAME ID
                    val updatedMovie = currentMovie.copy(
                        title = title,
                        description = description,
                        imageUri = updatedImageUri,
                        rating = newRating // Saving the updated rating
                    )
                    updatedMovie.id = currentMovie.id // Ensure Room knows which record to update

                    viewModel.updateMovie(updatedMovie) // This uses viewModelScope (Coroutine)

                    Toast.makeText(requireContext(), "Movie Updated!", Toast.LENGTH_SHORT).show()

                    // Go back to the list screen (clear the backstack if needed)
                    findNavController().popBackStack(R.id.allMoviesFragment, false)
                }
            } else {
                Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}