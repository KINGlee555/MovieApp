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
import androidx.recyclerview.widget.ItemTouchHelper.Callback.makeFlag
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.data.models.Movie
import com.example.movieapp.databinding.FragmentFavoritesBinding
import com.example.movieapp.ui.adapters.MovieAdapter
import com.example.movieapp.ui.viewmodel.MovieViewModel
import com.example.movieapp.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var binding: FragmentFavoritesBinding by autoCleared()
    private val viewModel: MovieViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val favoriteAdapter = MovieAdapter(object : MovieAdapter.OnMovieClickListener {
            override fun onMovieClick(id: Int) {
                findNavController().navigate(R.id.action_favoritesFragment_to_movieDetailsFragment, bundleOf("id" to id))
            }

            override fun onMovieLongClick(movie: Movie) {
                if (movie.isManualEntry) {
                    // יצירת Bundle עם האובייקט (הסרט כבר Parcelable)
                    val bundle = Bundle().apply {
                        putParcelable("movie", movie)
                    }
                    // ניווט למסך העריכה עם הנתונים
                    findNavController().navigate(R.id.action_favoritesFragment_to_editMovieFragment, bundle)
                } else {
                    // אופציונלי: להציג הודעה שלא ניתן לערוך סרטים מה-API
                    Toast.makeText(requireContext(), "ניתן לערוך רק סרטים שהוספו ידנית", Toast.LENGTH_SHORT).show()
                }
            }
        })

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
                val movie = favoriteAdapter.currentList[viewHolder.getBindingAdapterPosition()]
                val updatedMovie = movie.copy(isFavorite = false)
                viewModel.updateMovieStatus(updatedMovie)
                Toast.makeText(requireContext(), "הוסר מהמועדפים", Toast.LENGTH_SHORT).show()

            }
        }).attachToRecyclerView(binding.Favorites)

        binding.Favorites.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favoriteAdapter
        }

        viewModel.favoriteMovies.observe(viewLifecycleOwner) { movies ->
            favoriteAdapter.submitList(movies)
        }

        binding.fabAddMovie.setOnClickListener {
            findNavController().navigate(R.id.action_favoritesFragment_to_addMovieFragment)
        }
    }
}
/*
הדף מציג למשתמש את רשימת הסרטים שהוא הכי
אהב. המידע כאן מגיע רק מבסיס הנתונים המקומי (Room) ולא מהאינטרנט,
 מה שאומר שהרשימה הזו זמינה גם ללא חיבור לרשת.

 */