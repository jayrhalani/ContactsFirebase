package com.arvaan.contactsfirebase.activities.contact_activity

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arvaan.contactsfirebase.data.ContactRepository
import com.arvaan.contactsfirebase.utils.DateTimeUtils
import com.arvaan.contactsfirebase.utils.STORAGE_FOLDER_NAME
import java.util.*

class ContactViewModel(application: Application) : AndroidViewModel(application) {

//    companion object {
//        private val TAG by lazy { ContactViewModel::class.java.simpleName }
//    }

    private var contactRepository: ContactRepository = ContactRepository()

    private var firebaseDatabaseKey: MutableLiveData<String> = MutableLiveData()
    private var firstName: MutableLiveData<String> = MutableLiveData()
    private var lastName: MutableLiveData<String> = MutableLiveData()
    private var emailAddress: MutableLiveData<String> = MutableLiveData()
    private var phoneNumber: MutableLiveData<String> = MutableLiveData()
    private var imageWebUrl: MutableLiveData<String> = MutableLiveData()

    private var imageUri: MutableLiveData<Uri> = MutableLiveData()
    private var birthDate: MutableLiveData<String> = MutableLiveData()
    private var previousBirthDate: MutableLiveData<String> = MutableLiveData()
    private var activityType: MutableLiveData<String> = MutableLiveData()

    private val calender: Calendar = Calendar.getInstance()
    val currentDay = calender.get(Calendar.DAY_OF_MONTH)
    val currentMonth = calender.get(Calendar.MONTH)
    val currentYear = calender.get(Calendar.YEAR)


    fun getFirstName(): MutableLiveData<String> {
        return firstName
    }

    fun getLastName(): MutableLiveData<String> {
        return lastName
    }

    fun getEmailAddress(): MutableLiveData<String> {
        return emailAddress
    }

    fun getPhoneNumber(): MutableLiveData<String> {
        return phoneNumber
    }

    fun getImageWebUrl(): MutableLiveData<String> {
        return imageWebUrl
    }

    fun getBirthDate(): MutableLiveData<String> {
        return birthDate
    }

    fun getPreviousBirthDate(): MutableLiveData<String> {
        return previousBirthDate
    }

    fun getImageUri(): MutableLiveData<Uri> {
        return imageUri
    }

    fun getActivityType(): MutableLiveData<String> {
        return activityType
    }

    fun getFirebaseDatabaseKey(): MutableLiveData<String> {
        return firebaseDatabaseKey
    }

    fun saveDataToFirebase(
        firstName: String, lastName: String, emailId: String,
        phoneNumber: String, imageExtension: String
    ): LiveData<String> {

        val birthDateString = DateTimeUtils().getBirthDateString(getBirthDate().value!!)
        var imageTitle: String = firstName + "_" + lastName + "_" + birthDateString
        imageTitle = imageTitle.replace("\\s".toRegex(), "_")
        val storagePath = "$STORAGE_FOLDER_NAME/$imageTitle.$imageExtension"

        return contactRepository.saveDataToFirebase(
            imageUri = getImageUri().value!!, storagePath = storagePath,
            firstName = firstName, lastName = lastName, emailId = emailId,
            phoneNumber = phoneNumber, birthDate = getBirthDate().value!!
        )
    }


    fun updateContact(
        firstName: String,
        lastName: String,
        emailAddress: String,
        phoneNumber: String,
        imageExtension: String
    ): LiveData<String> {

        if (getImageUri().value != null) {
            val birthDateString = DateTimeUtils().getBirthDateString(getBirthDate().value!!)
            var imageTitle: String = firstName + "_" + lastName + "_" + birthDateString
            imageTitle = imageTitle.replace("\\s".toRegex(), "_")
            val storagePath = "$STORAGE_FOLDER_NAME/$imageTitle.$imageExtension"
            return contactRepository.updateContactWithNewImage(
                databaseKey = getFirebaseDatabaseKey().value!!,
                imageUri = getImageUri().value!!,
                imageUrl = getImageWebUrl().value!!,
                StoragePath = storagePath,
                firstName = firstName,
                lastName = lastName,
                emailId = emailAddress,
                phoneNumber = phoneNumber, birthDate = getBirthDate().value!!
            )
        } else {
            return contactRepository.updateContactWithoutNewImage(
                databaseKey = getFirebaseDatabaseKey().value!!,
                imageUrl = getImageWebUrl().value!!,
                firstName = firstName,
                lastName = lastName,
                emailId = emailAddress,
                phoneNumber = phoneNumber, birthDate = getBirthDate().value!!
            )
        }
    }

    fun deleteContact(): LiveData<String> {
        return contactRepository.deleteContact(
            databaseKey = getFirebaseDatabaseKey().value!!,
            imageUrl = getImageWebUrl().value!!
        )
    }
}