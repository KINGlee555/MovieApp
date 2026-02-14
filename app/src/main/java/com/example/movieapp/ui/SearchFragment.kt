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
import com.example.movieapp.data.models.Movie
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
            override fun onMovieClick(id: Int ) {
                findNavController().navigate(R.id.action_searchFragment_to_movieDetailsFragment, bundleOf("id" to id))
            }
            override fun onMovieLongClick(movie: Movie) {
                if (movie.isManualEntry) {
                    // יצירת Bundle עם האובייקט (הסרט כבר Parcelable)
                    val bundle = Bundle().apply {
                        putParcelable("movie", movie)
                    }
                    // ניווט למסך העריכה עם הנתונים
                    findNavController().navigate(R.id.action_searchFragment_to_editMovieFragment, bundle)
                } else {
                    // אופציונלי: להציג הודעה שלא ניתן לערוך סרטים מה-API
                    Toast.makeText(requireContext(), "ניתן לערוך רק סרטים שהוספו ידנית", Toast.LENGTH_SHORT).show()
                }
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
                is Loading -> {
                    binding.progressBar.isVisible = true
                    binding.NoResults.isVisible = false
                }
                is Success -> {
                    binding.progressBar.isVisible = false
                    val movies = resource.status.data ?: emptyList()
                    if (movies.isEmpty()) {
                        // No results found: show empty state, hide list
                        binding.NoResults.isVisible = true
                        binding.SearchResults.isVisible = false}
                    else{
                        binding.NoResults.isVisible = false
                        binding.SearchResults.isVisible = true
                        searchAdapter.submitList(movies)
                    }
                }
                is Error -> {
                    binding.progressBar.isVisible = false
                    binding.NoResults.isVisible = false
                    Toast.makeText(requireContext(), resource.status.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}