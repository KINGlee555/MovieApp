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

    private val viewModel: MovieViewModel by activityViewModels()

    private var _binding: FragmentAllMoviesBinding? = null
    private val binding get() = _binding!!

    // Define two adapters: one for public movies and one for user movies
    private lateinit var userAdapter: MovieAdapter
    private lateinit var publicAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()

        // 1. Observe public movies (Recommended section)
        viewModel.publicMovies?.observe(viewLifecycleOwner) { movieList ->
            publicAdapter.setMovies(movieList)
        }

        // 2. Observe user movies (My Collection section)
        viewModel.userMovies?.observe(viewLifecycleOwner) { movieList ->
            userAdapter.setMovies(movieList)
        }

        // Navigate to Add Movie screen
        binding.addFab.setOnClickListener {
            findNavController().navigate(R.id.action_allMovies_to_addMovie)
        }
    }

    private fun setupRecyclerViews() {
        // Shared listener logic
        val movieListener = object : MovieAdapter.MovieListener {
            override fun onMovieClicked(movie: Movie) {
                viewModel.setMovie(movie)
                findNavController().navigate(R.id.action_allMovies_to_movieDetails)
            }

            override fun onMovieLongClicked(movie: Movie) {
                // We only allow editing/long click for user movies (isPublic = false)
                if (!movie.isPublic) {
                    viewModel.setMovie(movie)
                    findNavController().navigate(R.id.action_allMovies_to_editMovie)
                }
            }
        }

        // Setup Public Movies (Horizontal)
        publicAdapter = MovieAdapter(emptyList(), movieListener)
        binding.publicMovies.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = publicAdapter
        }

        // Setup User Movies (Vertical)
        userAdapter = MovieAdapter(emptyList(), movieListener)
        binding.userMovies.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
        }

        // Swipe to Delete - Only for user movies
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val movieToDelete = userAdapter.getMovieAt(position)
                    viewModel.deleteMovie(movieToDelete)
                }
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.userMovies)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}