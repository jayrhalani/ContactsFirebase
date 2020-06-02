package com.arvaan.contactsfirebase.activities.main_activity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arvaan.contactsfirebase.data.Contact
import com.arvaan.contactsfirebase.data.ContactRepository
import com.google.firebase.database.DataSnapshot

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val contactRepository: ContactRepository = ContactRepository()
    private var contactList: LiveData<List<Contact>> =
        contactRepository.getFirebaseContactList()

    fun getContactList(): LiveData<List<Contact>> {
        return contactList
    }
}