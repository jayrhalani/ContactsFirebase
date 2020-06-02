package com.arvaan.contactsfirebase.activities.display_contact_activity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class DisplayContactViewModel(application: Application) : AndroidViewModel(application) {
    private var firebaseDatabaseKey: MutableLiveData<String> = MutableLiveData()
    private var firstName: MutableLiveData<String> = MutableLiveData()
    private var lastName: MutableLiveData<String> = MutableLiveData()
    private var fullName: MutableLiveData<String> = MutableLiveData()
    private var emailAddress: MutableLiveData<String> = MutableLiveData()
    private var phoneNumber: MutableLiveData<String> = MutableLiveData()
    private var birthDate: MutableLiveData<String> = MutableLiveData()
    private var imageUrl: MutableLiveData<String> = MutableLiveData()

    fun getFirebaseDatabaseKey(): MutableLiveData<String> {
        return firebaseDatabaseKey
    }

    fun getFirstName(): MutableLiveData<String> {
        return firstName
    }

    fun getLastName(): MutableLiveData<String> {
        return lastName
    }

    fun getFullName(): MutableLiveData<String> {
        return fullName
    }

    fun getEmailAddress(): MutableLiveData<String> {
        return emailAddress
    }

    fun getPhoneNumber(): MutableLiveData<String> {
        return phoneNumber
    }

    fun getBirthDate(): MutableLiveData<String> {
        return birthDate
    }

    fun getImageUrl(): MutableLiveData<String> {
        return imageUrl
    }
}