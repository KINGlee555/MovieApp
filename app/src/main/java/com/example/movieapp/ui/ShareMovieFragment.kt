package com.example.movieapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
class ShareMovieFragment : Fragment() {

    private var binding: FragmentShareMovieBinding by autoCleared()
    private val viewModel: ContactsViewModel by viewModels()
    private var movieTitle: String? = null


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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShareMovieBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentShareMovieBinding.bind(view)


        movieTitle = arguments?.getString("movieTitle")

        setupRecyclerView()
        checkPermissionAndLoad()
    }

    private fun setupRecyclerView() {

        val adapter = ContactsAdapter(emptyList()) { contact ->
            movieTitle?.let { title ->
                sendSms(contact.phoneNumber, title)
            }
        }
        binding.recyclerViewContacts.adapter = adapter


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
        binding.searchViewContacts.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterContacts(newText ?: "")
                return true
            }
        })
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

    /**
     * Helper function to launch the device's SMS application with a pre-filled recommendation message.
     * Uses Intent.ACTION_SENDTO to ensure only SMS apps handle the intent.
     */
    private fun sendSms(phoneNumber: String, movieName: String) {
        // Constructing the localized message using string resources for full localization support
        val message = getString(R.string.share_movie_message, movieName)

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$phoneNumber")
            putExtra("sms_body", message)
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            // Localization fix: Replacing hardcoded error message with getString
            Toast.makeText(requireContext(), getString(R.string.error_cannot_send_sms), Toast.LENGTH_SHORT).show()
        }
    }

}


