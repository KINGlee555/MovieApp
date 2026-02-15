package com.example.movieapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentShareMovieBinding
import com.example.movieapp.ui.adapters.ContactsAdapter
import com.example.movieapp.ui.viewmodel.ContactsViewModel
import com.example.movieapp.utils.Error
import com.example.movieapp.utils.Loading
import com.example.movieapp.utils.Success
import com.example.movieapp.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShareMovieFragment : Fragment(R.layout.fragment_share_movie) {

    private var binding: FragmentShareMovieBinding by autoCleared()
    private val viewModel: ContactsViewModel by viewModels()
    private var movieTitle: String? = null

    // ניהול הרשאות כפי שהמרצה ביקש
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.loadContacts()
        } else {
            Toast.makeText(
                requireContext(),
                "Permission denied to read contacts",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentShareMovieBinding.bind(view)

        // שליפת שם הסרט מה-Bundle (בלי navArgs)
        movieTitle = arguments?.getString("movieTitle")

        setupRecyclerView()
        checkPermissionAndLoad()
    }

    private fun setupRecyclerView() {
        // אתחול האדפטר המתוקן עם ה-Listener
        val adapter = ContactsAdapter(emptyList()) { contact ->
            movieTitle?.let { title ->
                sendSms(contact.phoneNumber, title)
            }
        }
        binding.recyclerViewContacts.adapter = adapter

        // צפייה בנתונים דרך ה-Resource (Loading/Success/Error)
        viewModel.contacts.observe(viewLifecycleOwner) { resource ->
            when (val status = resource.status) {
                is Loading -> {
                    binding.progressBar.isVisible = true
                }

                is Success -> {
                    binding.progressBar.isVisible = false
                    status.data?.let {
                        adapter.setList(it)
                    }
                }

                is Error -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkPermissionAndLoad() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.loadContacts()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    // פונקציית השליחה הפשוטה שביקשת
    private fun sendSms(phoneNumber: String, movieName: String) {
        val message = "היי! אני ממליץ לך לצפות בסרט: $movieName. הוא ממש טוב!"
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$phoneNumber") // פתיחת אפליקציית ה-SMS
            putExtra("sms_body", message)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "לא ניתן לשלוח הודעה", Toast.LENGTH_SHORT).show()
        }
    }
}

