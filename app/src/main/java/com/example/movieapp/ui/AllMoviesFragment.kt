package com.example.movieapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.data.Movie
import com.example.movieapp.databinding.FragmentAllMoviesBinding
import com.example.movieapp.viewmodel.MovieViewModel

class AllMoviesFragment : Fragment() {

    // Share the same ViewModel instance across all fragments in the activity
    private val viewModel: MovieViewModel by activityViewModels()

    private var _binding: FragmentAllMoviesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        // Observe the LiveData from ViewModel
        viewModel.allMovies?.observe(viewLifecycleOwner) { movieList ->
            // Update the adapter with the new list of movies
            adapter.setMovies(movieList)
        }

        // Navigate to Add Movie screen
        binding.addFab.setOnClickListener {
            findNavController().navigate(R.id.action_allMovies_to_addMovie)
        }
    }

    private fun setupRecyclerView() {
        // Initialize adapter with an empty list and click listeners
        adapter = MovieAdapter(emptyList(), object : MovieAdapter.MovieListener {
            override fun onMovieClicked(movie: Movie) {
                viewModel.setMovie(movie) // Update the shared 'chosenMovie'
                findNavController().navigate(R.id.action_allMovies_to_movieDetails)
            }

            override fun onMovieLongClicked(movie: Movie) {
                viewModel.setMovie(movie)
                findNavController().navigate(R.id.action_allMovies_to_editMovie)
            }
        })

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@AllMoviesFragment.adapter
        }

        // Implement Swipe to Delete
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Use bindingAdapterPosition to get the current position in the adapter
                val position = viewHolder.bindingAdapterPosition

                if (position != RecyclerView.NO_POSITION) {
                    val movieToDelete = adapter.getMovieAt(position)

                    // Call ViewModel to delete from Room
                    viewModel.deleteMovie(movieToDelete)
                }
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.recycler)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}