package com.example.movieapp.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentMovieDetailsBinding
import com.example.movieapp.viewmodel.MovieViewModel

class MovieDetailsFragment : Fragment() {

    private val viewModel: MovieViewModel by activityViewModels()

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.chosenMovie.observe(viewLifecycleOwner) { movie ->
            movie?.let { currentMovie ->
                binding.movieTitleDetails.text = currentMovie.title
                binding.movieDescDetails.text = currentMovie.description
                binding.movieRatingDetails.rating = currentMovie.rating

                // Handle Image Loading
                Glide.with(requireContext())
                    .load(currentMovie.imageUri)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(binding.movieImageDetails)

                //  for Public vs User Movies
                if (currentMovie.isPublic) {
                    // It's a public movie: show Add button, hide Edit/Delete
                    binding.btnAddToMyCollection.visibility = View.VISIBLE
                    binding.btnEditMovie.visibility = View.GONE
                    binding.btnDeleteMovie.visibility = View.GONE

                    binding.btnAddToMyCollection.setOnClickListener {
                        // Create a copy with isPublic = false and reset ID for new entry
                        val movieToSave = currentMovie.copy(isPublic = false).apply { id = 0 }
                        viewModel.addMovie(movieToSave)
                        Toast.makeText(requireContext(), "Added to your collection!", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                } else {
                    // It's a user movie: hide Add button, show Edit/Delete
                    binding.btnAddToMyCollection.visibility = View.GONE
                    binding.btnEditMovie.visibility = View.VISIBLE
                    binding.btnDeleteMovie.visibility = View.VISIBLE
                }
            }
        }

        binding.btnEditMovie.setOnClickListener {
            findNavController().navigate(R.id.action_movieDetails_to_editMovie)
        }

        binding.btnDeleteMovie.setOnClickListener {
            showDeleteDialog()
        }
    }

    private fun showDeleteDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm delete")
        builder.setMessage("Are you sure you want to delete this movie?")

        builder.setPositiveButton("Yes") { _, _ ->
            viewModel.chosenMovie.value?.let { movie ->
                viewModel.deleteMovie(movie)
                Toast.makeText(requireContext(), "Movie deleted", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }

        builder.setNegativeButton("No", null)
        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}