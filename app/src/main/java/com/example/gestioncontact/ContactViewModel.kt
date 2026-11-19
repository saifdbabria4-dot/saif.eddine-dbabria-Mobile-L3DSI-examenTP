package com.example.gestioncontact

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.gestioncontact.data.ContactDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = ContactDao(application)

    private val _contacts = MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>> get() = _contacts

    var selectedContactId: Long = -1L

    init {
        loadContacts()
    }

    fun loadContacts() {
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) {
                dao.getAll()
            }
            _contacts.value = list
        }
    }

    fun searchContacts(query: String) {
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) {
                if (query.isBlank()) dao.getAll() else dao.search(query)
            }
            _contacts.value = list
        }
    }

    fun insertContact(contact: Contact, onComplete: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dao.insert(contact)
            }
            loadContacts()
            onComplete()
        }
    }

    fun updateContact(contact: Contact, onComplete: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dao.update(contact)
            }
            loadContacts()
            onComplete()
        }
    }

    fun deleteContact(id: Long, onComplete: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dao.delete(id)
            }
            loadContacts()
            onComplete()
        }
    }
}
