package com.example.movieapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.data.models.Movie
import com.example.movieapp.databinding.FragmentWatchListBinding
import com.example.movieapp.ui.adapters.MovieAdapter
import com.example.movieapp.ui.viewmodel.MovieViewModel
import com.example.movieapp.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WatchListFragment : Fragment() {

    private var binding: FragmentWatchListBinding by autoCleared()
    private val viewModel: MovieViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWatchListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val watchListAdapter = MovieAdapter(object : MovieAdapter.OnMovieClickListener {
            override fun onMovieClick(movie: Movie) {
                findNavController().navigate(R.id.action_watchListFragment_to_movieDetailsFragment, bundleOf("id" to movie.id))
            }
        })

        val historyAdapter = MovieAdapter(object : MovieAdapter.OnMovieClickListener {
            override fun onMovieClick(movie: Movie) {
                findNavController().navigate(R.id.action_watchListFragment_to_movieDetailsFragment, bundleOf("id" to movie.id))
            }
        })

        binding.WatchList.layoutManager = LinearLayoutManager(requireContext())
        binding.WatchList.adapter = watchListAdapter

        binding.History.layoutManager = LinearLayoutManager(requireContext())
        binding.History.adapter = historyAdapter

        viewModel.watchedMovies.observe(viewLifecycleOwner) { movies ->
            watchListAdapter.submitList(movies.filter { !it.isWatched })
            historyAdapter.submitList(movies.filter { it.isWatched })
        }
    }
}