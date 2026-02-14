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
import com.example.movieapp.databinding.FragmentAllMoviesBinding
import com.example.movieapp.ui.adapters.MovieAdapter
import com.example.movieapp.ui.viewmodel.MovieViewModel
import com.example.movieapp.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllMoviesFragment : Fragment(), MovieAdapter.OnMovieClickListener {
    private var binding: FragmentAllMoviesBinding by autoCleared()
    private val viewModel: MovieViewModel by viewModels()
    private  lateinit var adapter: MovieAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllMoviesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = MovieAdapter(this)
        binding.AllMovies.layoutManager = LinearLayoutManager(requireContext())
        binding.AllMovies.adapter = adapter

        // טיפול ב-Resource (מצבי הצלחה/טעינה/שגיאה)
        viewModel.allMovies.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                is Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(resource.status.data)
                }
                is Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.status.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        setupNavigation()

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
                // שימוש ב-currentList של ה-ListAdapter - הכי פשוט ומהיר
                val movie = adapter.currentList[viewHolder.getBindingAdapterPosition()]

                if (movie.isManualEntry) {
                    // קריאה לפונקציה ב-ViewModel שמריצה Coroutine ברקע
                    viewModel.deleteMovie(movie)
                    Toast.makeText(requireContext(), "Movie deleted", Toast.LENGTH_SHORT).show()
                } else {
                    // החזרה למקום אם זה מה-API
                    adapter.notifyItemChanged(viewHolder.getBindingAdapterPosition())
                    Toast.makeText(requireContext(), "Cannot delete API movies", Toast.LENGTH_SHORT).show()
                }
            }
        }).attachToRecyclerView(binding.AllMovies)
    }

    override fun onMovieClick(id: Int) {
        findNavController().navigate(R.id.action_allMoviesFragment_to_movieDetailsFragment, bundleOf("id" to id))
    }
    override fun onMovieLongClick(movie: Movie) {
        if (movie.isManualEntry) {
            val bundle = Bundle().apply {
                putParcelable("movie", movie)
            }
            findNavController().navigate(R.id.action_allMoviesFragment_to_editMovieFragment, bundle)
        } else {
            Toast.makeText(requireContext(), "ניתן לערוך רק סרטים שהוספו ידנית", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupNavigation() {
        binding.btnNavFav.setOnClickListener { findNavController().navigate(R.id.action_allMoviesFragment_to_favoritesFragment) }
        binding.btnNavWatch.setOnClickListener { findNavController().navigate(R.id.action_allMoviesFragment_to_watchListFragment) }
        binding.btnNavCinema.setOnClickListener { findNavController().navigate(R.id.action_allMoviesFragment_to_cinemaFragment) }
        binding.btnNavSearch.setOnClickListener { findNavController().navigate(R.id.action_allMoviesFragment_to_searchFragment) }
    }
}