package com.example.movieapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentSearchBinding
import com.example.movieapp.ui.adapters.MovieAdapter
import com.example.movieapp.ui.viewmodel.MovieViewModel
import com.example.movieapp.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var binding: FragmentSearchBinding by autoCleared()
    private val viewModel: MovieViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchAdapter = MovieAdapter(object : MovieAdapter.OnMovieClickListener {
            override fun onMovieClick(movie: com.example.movieapp.data.models.Movie) {
                val bundle = bundleOf("id" to movie.id)
                findNavController().navigate(R.id.action_searchFragment_to_movieDetailsFragment, bundle)
            }
        })

        binding.SearchResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchMovies(it) }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean = true
        })

        viewModel.searchResults.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                is Loading -> binding.progressBar.isVisible = true
                is Success -> {
                    binding.progressBar.isVisible = false
                    searchAdapter.submitList(resource.status.data)
                }
                is Error -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(), resource.status.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}