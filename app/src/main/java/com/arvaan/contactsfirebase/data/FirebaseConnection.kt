package com.arvaan.contactsfirebase.data

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arvaan.contactsfirebase.utils.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebaseConnection {

    private var mDatabase: DatabaseReference? = FirebaseDatabase
        .getInstance()
        .getReference(DATABASE_PATH_CONTACT)
    private var storageReference: StorageReference? = FirebaseStorage.getInstance().reference

    companion object {
        private val TAG by lazy { FirebaseConnection::class.java.simpleName }
    }

    fun getContactList(): LiveData<List<Contact>> {
        val contactLiveData: MutableLiveData<List<Contact>> = MutableLiveData()
        val contactList = ArrayList<Contact>()
        mDatabase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapShot: DataSnapshot) {
                contactList.clear()
                for (snapShot in dataSnapShot.children) {
                    val contact: Contact = snapShot.getValue(Contact::class.java)!!
                    contactList.add(contact)
                }
                contactLiveData.value = contactList
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Exception: ${databaseError.message}")
            }

        })
        return contactLiveData
    }

    fun saveDataToFirebase(
        imageUri: Uri, storagePath: String, firstName: String,
        lastName: String, emailId: String, phoneNumber: String, birthDate: String
    ): LiveData<String> {

        // Check KEYS --> FIREBASE_STATUS_STARTED, FIREBASE_COMPLETED_SAVING_CONTACT,
        //  FIREBASE_FAILED_SAVING_CONTACT

        val statusLiveData: MutableLiveData<String> = MutableLiveData()
        var status: String = FIREBASE_STATUS_STARTED
        statusLiveData.value = status

        val uploadStorageReference: StorageReference = storageReference!!.child(storagePath)
        val uploadTask = uploadStorageReference.putFile(imageUri)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {

                status = FIREBASE_FAILED_SAVING_CONTACT
                statusLiveData.value = status

                task.exception?.let {
                    throw it
                }
            }
            uploadStorageReference.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUrl: String = task.result.toString()
                val uploadId: String = mDatabase!!.push().key!!
                val contact = Contact(
                    key = uploadId, avatar = downloadUrl, firstName = firstName,
                    lastName = lastName, phoneNumber = phoneNumber, emailAddress = emailId,
                    birthDate = birthDate
                )
                mDatabase!!.child(uploadId).setValue(contact)
                status = FIREBASE_COMPLETED_SAVING_CONTACT
                statusLiveData.value = status
            } else {
                status = FIREBASE_FAILED_SAVING_CONTACT
                statusLiveData.value = status
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, exception.message!!)
            status = FIREBASE_FAILED_SAVING_CONTACT
            statusLiveData.value = status
        }
        return statusLiveData
    }

    fun updateContactWithNewImage(
        databaseKey: String,
        imageUri: Uri, imageUrl: String, storagePath: String, firstName: String,
        lastName: String, emailId: String, phoneNumber: String, birthDate: String
    ): LiveData<String> {

        // Check KEYS --> FIREBASE_STATUS_STARTED,
        //  FIREBASE_COMPLETED_DELETING_IMAGE, FIREBASE_FAILED_DELETING_IMAGE
        //  FIREBASE_COMPLETED_UPLOADING_IMAGE, FIREBASE_FAILED_UPLOADING_IMAGE,
        //  FIREBASE_COMPLETED_UPDATING_CONTACT, FIREBASE_FAILED_UPDATING_CONTACT

        val statusLiveData: MutableLiveData<String> = MutableLiveData()
        var status: String = FIREBASE_STATUS_STARTED
        statusLiveData.value = status

        // Check Deleting file from storage
        val deleteStorageReference: StorageReference = FirebaseStorage
            .getInstance().getReferenceFromUrl(imageUrl)
        deleteStorageReference.delete().addOnSuccessListener {
            status = FIREBASE_COMPLETED_DELETING_IMAGE
            statusLiveData.value = status

            // Check Uploading New Image
            val uploadStorageReference = storageReference!!.child(storagePath)
            val uploadTask = uploadStorageReference.putFile(imageUri)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {

                    status = FIREBASE_FAILED_UPLOADING_IMAGE
                    statusLiveData.value = status

                    task.exception?.let {
                        throw it
                    }
                }
                uploadStorageReference.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    status = FIREBASE_COMPLETED_UPLOADING_IMAGE
                    statusLiveData.value = status

                    val downloadUrl: String = task.result.toString()

                    // Check Updating contact on Database
                    val contact = Contact(
                        key = databaseKey, avatar = downloadUrl, firstName = firstName,
                        lastName = lastName, phoneNumber = phoneNumber, emailAddress = emailId,
                        birthDate = birthDate
                    )
                    mDatabase!!.child(databaseKey)
                        .setValue(contact).addOnSuccessListener {
                            status = FIREBASE_COMPLETED_UPDATING_CONTACT
                            statusLiveData.value = status
                        }.addOnFailureListener {
                            status = FIREBASE_FAILED_UPDATING_CONTACT
                            statusLiveData.value = status
                        }
                } else {
                    status = FIREBASE_FAILED_UPLOADING_IMAGE
                    statusLiveData.value = status
                }
            }.addOnFailureListener { error ->
                Log.e(TAG, "Exception: ${error.message}")
                status = FIREBASE_FAILED_UPLOADING_IMAGE
                statusLiveData.value = status
            }
        }.addOnFailureListener { error ->
            Log.e(TAG, "Exception: ${error.message}")
            status = FIREBASE_FAILED_DELETING_IMAGE
            statusLiveData.value = status
        }
        return statusLiveData
    }

    fun updateContactWithoutNewImage(
        databaseKey: String,
        imageUrl: String, firstName: String,
        lastName: String, emailId: String, phoneNumber: String, birthDate: String
    ): LiveData<String> {

        // Check KEYS --> FIREBASE_STATUS_STARTED,
        //  FIREBASE_COMPLETED_UPDATING_CONTACT, FIREBASE_FAILED_UPDATING_CONTACT

        val statusLiveData: MutableLiveData<String> = MutableLiveData()
        var status: String = FIREBASE_STATUS_STARTED
        statusLiveData.value = status

        val contact = Contact(
            key = databaseKey, avatar = imageUrl, firstName = firstName, lastName = lastName,
            phoneNumber = phoneNumber, emailAddress = emailId, birthDate = birthDate
        )
        mDatabase!!.child(databaseKey).setValue(contact).addOnCompleteListener { task ->
            if (task.isComplete) {
                status = FIREBASE_COMPLETED_UPDATING_CONTACT
                statusLiveData.value = status
            }
        }.addOnFailureListener {
            status = FIREBASE_FAILED_UPDATING_CONTACT
            statusLiveData.value = status
        }
        return statusLiveData
    }

    fun deleteContact(
        databaseKey: String,
        imageUrl: String
    ): LiveData<String> {

        // Check KEYS --> FIREBASE_STATUS_STARTED,
        //  FIREBASE_COMPLETED_DELETING_IMAGE, FIREBASE_FAILED_DELETING_IMAGE,
        //  FIREBASE_COMPLETED_DELETING_CONTACT, FIREBASE_FAILED_DELETING_CONTACT

        val statusLiveData: MutableLiveData<String> = MutableLiveData()
        var status: String = FIREBASE_STATUS_STARTED
        statusLiveData.value = status

        val deleteStorageReference: StorageReference = FirebaseStorage.getInstance()
            .getReferenceFromUrl(imageUrl)

        deleteStorageReference.delete().addOnSuccessListener {

            status = FIREBASE_COMPLETED_DELETING_IMAGE
            statusLiveData.value = status
            mDatabase!!.child(databaseKey).removeValue().addOnSuccessListener {
                status = FIREBASE_COMPLETED_DELETING_CONTACT
                statusLiveData.value = status
            }.addOnFailureListener {
                status = FIREBASE_FAILED_DELETING_CONTACT
                statusLiveData.value = status
            }
        }.addOnFailureListener { error ->
            Log.e(TAG, "Exception: ${error.message}")
            status = FIREBASE_FAILED_DELETING_IMAGE
            statusLiveData.value = status
        }
        return statusLiveData
    }
}