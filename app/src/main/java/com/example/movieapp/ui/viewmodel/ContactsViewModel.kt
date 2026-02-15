package com.example.movieapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.models.Contact
import com.example.movieapp.utils.ContactHelper
import dagger.hilt.android.internal.Contexts.getApplication
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
                        _contacts.value = Resource.error("No contacts found")
                    } else {
                        _contacts.value = Resource.success(fetchedContacts)
                        allContacts = fetchedContacts
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _contacts.value = Resource.error(e.message ?: "An error occurred")
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
/*
ה-ContactsViewModel מאפשר למשוך את
רשימת אנשי הקשר מהמכשיר ולהציג אותם
למשתמש. המטרה היא לאפשר למשתמש לבחור
איש קשר ולשלוח לו המלצה על סרט (דרך SMS
או אפליקציות אחרות) בצורה מהירה ונוחה מבלי
לצאת מהאפליקציה.
 */