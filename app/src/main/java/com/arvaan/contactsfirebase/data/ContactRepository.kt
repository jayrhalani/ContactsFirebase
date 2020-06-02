package com.arvaan.contactsfirebase.data

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData

class ContactRepository {
    private var firebaseConnection: FirebaseConnection = FirebaseConnection()


    companion object {
        private val TAG by lazy { ContactRepository::class.java.simpleName }
    }

    fun getFirebaseContactList(): LiveData<List<Contact>> {
        return firebaseConnection.getContactList()
    }

    fun saveDataToFirebase(
        imageUri: Uri, storagePath: String, firstName: String,
        lastName: String, emailId: String, phoneNumber: String, birthDate: String
    ): LiveData<String> {
        return firebaseConnection.saveDataToFirebase(
            imageUri, storagePath, firstName,
            lastName, emailId, phoneNumber, birthDate
        )
    }

    fun updateContactWithNewImage(
        databaseKey: String,
        imageUri: Uri, imageUrl: String, StoragePath: String, firstName: String,
        lastName: String, emailId: String, phoneNumber: String, birthDate: String
    ): LiveData<String> {
        Log.e(TAG, "Path String: $StoragePath")
        return firebaseConnection.updateContactWithNewImage(
            databaseKey = databaseKey, imageUri = imageUri, imageUrl = imageUrl,
            storagePath = StoragePath, firstName = firstName, lastName = lastName,
            emailId = emailId, phoneNumber = phoneNumber, birthDate = birthDate
        )
    }

    fun updateContactWithoutNewImage(
        databaseKey: String,
        imageUrl: String, firstName: String,
        lastName: String, emailId: String, phoneNumber: String, birthDate: String
    ): LiveData<String> {
        return firebaseConnection.updateContactWithoutNewImage(
            databaseKey = databaseKey, imageUrl = imageUrl, firstName = firstName,
            lastName = lastName, emailId = emailId, phoneNumber = phoneNumber, birthDate = birthDate
        )
    }

    fun deleteContact(
        databaseKey: String,
        imageUrl: String
    ): LiveData<String> {
        return firebaseConnection.deleteContact(
            databaseKey = databaseKey, imageUrl = imageUrl
        )
    }

}