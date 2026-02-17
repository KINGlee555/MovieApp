package com.example.movieapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.data.models.Movie
import com.example.movieapp.databinding.FragmentWatchListBinding
import com.example.movieapp.ui.adapters.MovieAdapter
import com.example.movieapp.ui.viewmodel.MovieViewModel
import com.example.movieapp.utils.autoCleared
import com.example.movieapp.utils.Success
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
            override fun onMovieClick(id: Int) {
                findNavController().navigate(R.id.action_watchListFragment_to_movieDetailsFragment, bundleOf("id" to id))
            }
            override fun onMovieLongClick(movie: Movie) {
                if (movie.isManualEntry) {

                    val bundle = Bundle().apply {
                        putParcelable("movie", movie)
                    }

                    findNavController().navigate(R.id.action_allMoviesFragment_to_editMovieFragment, bundle)
                } else {

                    Toast.makeText(requireContext(), getString(R.string.edit_manual_only_error), Toast.LENGTH_SHORT).show()
                }
            }
        })

        val historyAdapter = MovieAdapter(object : MovieAdapter.OnMovieClickListener {
            override fun onMovieClick(id: Int) {
                Toast.makeText(requireContext(), getString(R.string.edit_manual_only_error), Toast.LENGTH_SHORT).show()
            }
            override fun onMovieLongClick(movie: Movie) {
                if (movie.isManualEntry) {

                    val bundle = Bundle().apply {
                        putParcelable("movie", movie)
                    }
                    findNavController().navigate(R.id.action_allMoviesFragment_to_editMovieFragment, bundle)
                }   else {
                // Notify the user that only manual entries can be edited
                Toast.makeText(requireContext(), getString(R.string.edit_manual_only_error), Toast.LENGTH_SHORT).show()
            }
            }
        })

        binding.WatchList.layoutManager = LinearLayoutManager(requireContext())
        binding.WatchList.adapter = watchListAdapter

        binding.History.layoutManager = LinearLayoutManager(requireContext())
        binding.History.adapter = historyAdapter

        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) = makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val movie =watchListAdapter.currentList[viewHolder.getBindingAdapterPosition()]
                val updatedMovie = movie.copy(isInWatchList = false, isWatched = false)
                viewModel.updateMovieStatus(updatedMovie)
                Toast.makeText(requireContext(),
                    getString(R.string.removed_from_watch_list), Toast.LENGTH_SHORT).show()

            }
        }).attachToRecyclerView(binding.WatchList)

        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) = makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val movie = historyAdapter.currentList[viewHolder.getBindingAdapterPosition()]
                val updatedMovie = movie.copy(isInWatchList = false, isWatched = false)
                viewModel.updateMovieStatus(updatedMovie)
                Toast.makeText(requireContext(),
                    getString(R.string.removed_from_watch_history), Toast.LENGTH_SHORT).show()

            }
        }).attachToRecyclerView(binding.History)

        viewModel.allMovies.observe(viewLifecycleOwner) { resource ->
            if (resource.status is Success) {
                resource.status.data.let { movies ->
                        val watchList = movies?.filter { it.isInWatchList && !it.isWatched }
                        watchListAdapter.submitList(watchList)

                }
            }
        }

        viewModel.watchedMovies.observe(viewLifecycleOwner) { movies ->
            historyAdapter.submitList(movies)
        }
    }
}
