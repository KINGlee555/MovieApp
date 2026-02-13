package com.example.movieapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentCinemaBinding
import com.example.movieapp.utils.autoCleared

class CinemaFragment : Fragment(R.layout.fragment_cinema) {

    private var binding: FragmentCinemaBinding by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCinemaBinding.bind(view)

        binding.btnFindCinema.setOnClickListener {
            // יצירת Intent לפתיחת מפות גוגל עם חיפוש מובנה של בתי קולנוע בסביבה
            val gmmIntentUri = Uri.parse("geo:0,0?q=cinema")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(mapIntent)
            }
        }
    }

}