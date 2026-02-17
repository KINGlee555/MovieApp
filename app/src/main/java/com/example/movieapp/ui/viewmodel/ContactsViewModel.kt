package com.example.movieapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.movieapp.R
import com.example.movieapp.data.models.Contact
import com.example.movieapp.utils.ContactHelper
import com.example.movieapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    val contactHelper =ContactHelper ()
    private val _contacts = MutableLiveData<Resource<List<Contact>>>()
    val contacts: LiveData<Resource<List<Contact>>> = _contacts
    private var allContacts: List<Contact> = emptyList()

    fun loadContacts() {
        _contacts.value = Resource.loading()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fetchedContacts = contactHelper.getContacts(getApplication())

                withContext(Dispatchers.Main) {
                    if (fetchedContacts.isEmpty()) {
                        _contacts.value = Resource.error(R.string.no_contacts_found.toString())
                    } else {
                        _contacts.value = Resource.success(fetchedContacts)
                        allContacts = fetchedContacts
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _contacts.value = Resource.error(e.message ?: R.string.an_error_occurred.toString())
                }
            }
        }
    }
    fun filterContacts(query: String) {
        val filtered = if (query.isEmpty()) {
            allContacts
        } else {
            allContacts.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.phoneNumber.contains(query)
            }
        }
        _contacts.value = Resource.success(filtered)
    }
}
