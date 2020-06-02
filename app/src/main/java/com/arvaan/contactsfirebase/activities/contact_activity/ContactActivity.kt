package com.arvaan.contactsfirebase.activities.contact_activity

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.arvaan.contactsfirebase.R
import com.arvaan.contactsfirebase.activities.BaseActivity
import com.arvaan.contactsfirebase.utils.*
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_contact.*


class ContactActivity : BaseActivity(R.layout.activity_contact), View.OnClickListener {

    companion object {
        private val TAG by lazy { ContactActivity::class.java.simpleName }
        private const val REQUEST_CODE_IMAGE_PICK = 100
//        private const val REQUEST_CODE_IMAGE_CAPTURE = 101

    }

    private lateinit var contactViewModel: ContactViewModel

    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            permissionRequestCount = it.getInt(KEY_PERMISSIONS_REQUEST_COUNT, 0)
        }
        requestPermissionsIfNecessary(permissions)
        initViews()
    }

    private fun initViews() {
        initializeAndObserveViewModel()
        getIntentData()

        cv_contact_edit_avatar.setOnClickListener(this)
        tv_birth_date.setOnClickListener(this)
        btn_save_contact.setOnClickListener(this)

        iv_contact_avatar.setPadding(30, 30, 30, 30)
    }

    private fun initializeAndObserveViewModel() {
        contactViewModel = ViewModelProvider(this).get(ContactViewModel::class.java)

        contactViewModel.getImageWebUrl().observe(this, Observer { imageUrl ->
            if (contactViewModel.getImageUri().value == null) {
                iv_contact_avatar.setPadding(0, 0, 0, 0)
                Glide.with(this).load(imageUrl).into(iv_contact_avatar)
            }
        })

        contactViewModel.getImageUri().observe(this, Observer { imageUri ->
            if (imageUri != null) {
                iv_contact_avatar.setPadding(0, 0, 0, 0)
                Glide.with(this).load(imageUri).into(iv_contact_avatar)
            }
        })

        contactViewModel.getFirstName().observe(this, Observer { firstName ->
            edt_first_name.setText(firstName)
        })

        contactViewModel.getLastName().observe(this, Observer { lastName ->
            edt_last_name.setText(lastName)
        })

        contactViewModel.getEmailAddress().observe(this, Observer { emailAddress ->
            edt_email_address.setText(emailAddress)
        })

        contactViewModel.getPhoneNumber().observe(this, Observer { phoneNumber ->
            edt_phone_number.setText(phoneNumber)
        })

        contactViewModel.getBirthDate().observe(this, Observer { birthDate ->
            tv_birth_date.text = birthDate
        })


    }

    private fun getIntentData() {
        if (intent.hasExtra(KEY_INTENT_SELECTION)) {
            if (intent.getStringExtra(KEY_INTENT_SELECTION) == KEY_INTENT_ADD) {
                title = getString(R.string.title_add_contact)
                contactViewModel.getActivityType().value = KEY_INTENT_ADD
            } else if (intent.getStringExtra(KEY_INTENT_SELECTION) == KEY_INTENT_UPDATE) {
                title = getString(R.string.title_update_contact)
                contactViewModel.getActivityType().value = KEY_INTENT_UPDATE
                contactViewModel.getFirebaseDatabaseKey().value =
                    intent.getStringExtra(KEY_DATABASE_ITEM_KEY)
                contactViewModel.getFirstName().value = intent.getStringExtra(KEY_FIRST_NAME)
                contactViewModel.getLastName().value = intent.getStringExtra(KEY_LAST_NAME)
                contactViewModel.getEmailAddress().value = intent.getStringExtra(KEY_EMAIL_ID)
                contactViewModel.getPhoneNumber().value = intent.getStringExtra(KEY_PHONE_NUMBER)
                contactViewModel.getBirthDate().value = intent.getStringExtra(KEY_BIRTH_DATE)
                contactViewModel.getPreviousBirthDate().value =
                    intent.getStringExtra(KEY_BIRTH_DATE)
                contactViewModel.getImageWebUrl().value = intent.getStringExtra(KEY_IMAGE_URL)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_PERMISSIONS_REQUEST_COUNT, permissionRequestCount)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            requestPermissionsIfNecessary(permissions) // no-op if permissions are granted already.
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_IMAGE_PICK -> data?.let { handleSelectImageResult(data) }
                // REQUEST_CODE_IMAGE_CAPTURE -> data!!.clipData?.let { handleCaptureImageResult(data.clipData!!) }
                else -> Log.d(TAG, "Unknown request code.")
            }
        } else {
            Log.e(TAG, String.format("Unexpected Result code %s", resultCode))
        }
    }

    private fun handleCaptureImageResult(intent: Intent) {
        // Check createImageFile From Retrieved Intent
        //  Compress this Image Size
        //  Set this imagePath to ViewModel
        //  --> contactViewModel.getImageUri().value = imageUri
        //  It will automatically set image to ImageView

    }

    private fun handleSelectImageResult(intent: Intent) {
        // If clipData is available, we use it, otherwise we use data
        val imageUri: Uri? = intent.clipData?.getItemAt(0)?.uri ?: intent.data

        if (imageUri == null) {
            Log.e(TAG, "Invalid input image Uri.")
            return
        } else {
            Log.e(TAG, "Image Uri While Selection: $imageUri")
            contactViewModel.getImageUri().value = imageUri
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return if (contactViewModel.getActivityType().value == KEY_INTENT_UPDATE) {
            val inflater: MenuInflater = menuInflater
            inflater.inflate(R.menu.contact_menu, menu)
            true
        } else {
            super.onCreateOptionsMenu(menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.contact_menu_delete -> {
                contactViewModel.deleteContact().observe(this, Observer { status ->
                    // Check KEYS --> FIREBASE_STATUS_STARTED,
                    //  FIREBASE_COMPLETED_DELETING_IMAGE, FIREBASE_FAILED_DELETING_IMAGE,
                    //  FIREBASE_COMPLETED_DELETING_CONTACT, FIREBASE_FAILED_DELETING_CONTACT
                    when (status) {
                        FIREBASE_STATUS_STARTED -> {
                            pb_contact_progressbar.visibility = View.VISIBLE
                            disableViews()
                            Log.e(TAG, "Deleting contact from firebase started")
                        }
                        FIREBASE_COMPLETED_DELETING_IMAGE -> {
                            Log.e(TAG, "Successfully Deleted Image from Firebase Store")
                        }
                        FIREBASE_FAILED_DELETING_IMAGE -> {
                            Log.e(TAG, "Failed to Delete Image from Firebase Store")
                            showToast(
                                getString(R.string.failed_deleting_image_from_firebase_store),
                                true
                            )
                            enableViews()
                            pb_contact_progressbar.visibility = View.GONE
                            finish()
                        }
                        FIREBASE_COMPLETED_DELETING_CONTACT -> {
                            Log.e(
                                TAG,
                                getString(R.string.successfully_deleted_contact_from_firebase_database)
                            )
                            pb_contact_progressbar.visibility = View.GONE
                            enableViews()
                            finish()
                        }
                        FIREBASE_FAILED_DELETING_CONTACT -> {
                            Log.e(TAG, "Failed to Delete contact from Firebase database")
                            showToast(
                                getString(R.string.failed_to_delete_contact_from_firebase_database),
                                true
                            )
                            pb_contact_progressbar.visibility = View.GONE
                            enableViews()
                            finish()
                        }
                        else -> {
                            pb_contact_progressbar.visibility = View.GONE
                            enableViews()
                            Log.e(TAG, "Something went Wrong!")
                            showToast(getString(R.string.something_went_wrong), true)
                            finish()
                        }

                    }
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            cv_contact_edit_avatar -> {
                selectFromGallery()
//                val popupMenu = PopupMenu(this, cv_contact_edit_avatar)
//                popupMenu.menuInflater.inflate(R.menu.popup_menu_main, popupMenu.menu)
//                popupMenu.setOnMenuItemClickListener { item: MenuItem? ->
//                    when (item!!.itemId) {
//                        R.id.popup_menu_action_select_image -> {
//                            selectFromGallery()
//                            true
//                        }
//                        R.id.popup_menu_action_take_photo -> {
//                            captureImage()
//                            true
//                        }
//                        else -> {
//                            false
//                        }
//
//                    }
//                }
//                popupMenu.show()
            }
            tv_birth_date -> {
                pickBirthDate()
            }
            btn_save_contact -> {
                when (contactViewModel.getActivityType().value) {
                    KEY_INTENT_UPDATE -> {
                        updateContactToFirebase()
                    }
                    KEY_INTENT_ADD -> {
                        addContactToFirebase()
                    }
                    else -> {
                        showToast(getString(R.string.something_went_wrong), true)
                    }
                }
            }
        }
    }

//    private fun captureImage() {
//        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        startActivityForResult(takePicture, REQUEST_CODE_IMAGE_CAPTURE)
//    }

    // Intent to select Image
    private fun selectFromGallery() {
        val chooseIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        chooseIntent.type = "image/*"
        startActivityForResult(
            chooseIntent,
            REQUEST_CODE_IMAGE_PICK
        )
    }

    // DatePicker
    private fun pickBirthDate() {
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog
                .OnDateSetListener { _, year, month, day ->
                    val birthDate = "${DateTimeUtils().toDoubleDigit(day)}/" +
                            "${DateTimeUtils().toDoubleDigit(month + 1)}/${DateTimeUtils().toDoubleDigit(
                                year
                            )}"
                    contactViewModel.getBirthDate().value = birthDate
                }, contactViewModel.currentYear,
            contactViewModel.currentMonth,
            contactViewModel.currentDay
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun updateContactToFirebase() {
        //Check if value of firstName, LastName, EmailAddress, PhoneNumber is not Empty
        // its not needed for imageUrl and BirthDate because it will never gonna null
        if (isEditTextEmpty(edt_first_name)) {
            showToast(getString(R.string.please_enter_first_name), true)
        } else if (isEditTextEmpty(edt_last_name)) {
            showToast(getString(R.string.please_enter_last_name), true)
        } else if (isEditTextEmpty(edt_phone_number)) {
            showToast(getString(R.string.please_enter_phone_number), true)
        } else if (!isValidPhoneNumber(edt_phone_number)) {
            showToast(getString(R.string.please_enter_valid_phone_number), true)
        } else if (isEditTextEmpty(edt_email_address)) {
            showToast(getString(R.string.please_enter_email_address), true)
        } else if (!isValidEmailId(edt_email_address)) {
            showToast(getString(R.string.please_enter_valid_email_address), true)
        } else {
            // Check if value of any editText, or BirthDate TextView or Image's Uri has been changed
            if (isEditTextValueUpdated(edt_first_name, contactViewModel.getFirstName().value!!) ||
                isEditTextValueUpdated(edt_last_name, contactViewModel.getLastName().value!!) ||
                isEditTextValueUpdated(
                    edt_email_address,
                    contactViewModel.getEmailAddress().value!!
                ) ||
                isEditTextValueUpdated(
                    edt_phone_number,
                    contactViewModel.getPhoneNumber().value!!
                ) ||
                contactViewModel.getBirthDate().value != contactViewModel.getPreviousBirthDate().value ||
                contactViewModel.getImageUri().value != null
            ) {
                val imageExtension: String = if (contactViewModel.getImageUri().value != null) {
                    Utils(this)
                        .getFileExtension(contactViewModel.getImageUri().value)
                } else {
                    // TODO -- Update this Logic
                    "Not Required"
                }

                contactViewModel.updateContact(
                    firstName = edt_first_name.text.toString(),
                    lastName = edt_last_name.text.toString(),
                    emailAddress = edt_email_address.text.toString(),
                    phoneNumber = edt_phone_number.text.toString(),
                    imageExtension = imageExtension
                ).observe(this, Observer { status ->

                    // Check KEYS --> FIREBASE_STATUS_STARTED,
                    //  FIREBASE_COMPLETED_DELETING_IMAGE, FIREBASE_FAILED_DELETING_IMAGE
                    //  FIREBASE_COMPLETED_UPLOADING_IMAGE, FIREBASE_FAILED_UPLOADING_IMAGE,
                    //  FIREBASE_COMPLETED_UPDATING_CONTACT, FIREBASE_FAILED_UPDATING_CONTACT

                    when (status) {
                        FIREBASE_STATUS_STARTED -> {
                            pb_contact_progressbar.visibility = View.VISIBLE
                            disableViews()
                            Log.e(TAG, "Updating contact started")
                        }
                        FIREBASE_COMPLETED_DELETING_IMAGE -> {
                            Log.e(TAG, "Successfully Deleted old Image from Firebase Store")
                        }
                        FIREBASE_FAILED_DELETING_IMAGE -> {
                            Log.e(TAG, "Failed to Delete old Image from Firebase Store")
                            showToast("Failed to Delete old Image from Firebase Store", true)
                            pb_contact_progressbar.visibility = View.GONE
                            enableViews()
                            finish()
                        }
                        FIREBASE_COMPLETED_UPLOADING_IMAGE -> {
                            Log.e(TAG, "Successfully Inserted new Image to Firebase Store")
                        }
                        FIREBASE_FAILED_UPLOADING_IMAGE -> {
                            Log.e(TAG, "Failed to Insert new Image to Firebase Store")
                            showToast("Failed to Insert new Image to Firebase Store", true)
                            pb_contact_progressbar.visibility = View.GONE
                            enableViews()
                            finish()
                        }
                        FIREBASE_COMPLETED_UPDATING_CONTACT -> {
                            Log.e(TAG, "Successfully Updated contact to firebase Database")
                            pb_contact_progressbar.visibility = View.GONE
                            enableViews()
                            finish()
                        }
                        FIREBASE_FAILED_UPDATING_CONTACT -> {
                            Log.e(TAG, "Failed Updating contact to firebase Database")
                            showToast("Failed Updating contact to firebase Database", true)
                            pb_contact_progressbar.visibility = View.GONE
                            enableViews()
                            finish()
                        }
                        else -> {
                            pb_contact_progressbar.visibility = View.GONE
                            enableViews()
                            Log.e(TAG, "Something went Wrong!")
                            showToast(getString(R.string.something_went_wrong), true)
                            finish()
                        }
                    }
                })
            } else {
                showToast("Nothing has been changed", true)
                finish()
            }
        }
    }

    private fun addContactToFirebase() {
        when {
            isEditTextEmpty(edt_first_name) -> {
                showToast(getString(R.string.please_enter_first_name), true)
            }
            isEditTextEmpty(edt_last_name) -> {
                showToast(getString(R.string.please_enter_last_name), true)
            }
            isEditTextEmpty(edt_phone_number) -> {
                showToast(getString(R.string.please_enter_phone_number), true)
            }
            !isValidPhoneNumber(edt_phone_number) -> {
                showToast(getString(R.string.please_enter_valid_phone_number), true)
            }
            isEditTextEmpty(edt_email_address) -> {
                showToast(getString(R.string.please_enter_email_address), true)
            }
            !isValidEmailId(edt_email_address) -> {
                showToast(getString(R.string.please_enter_valid_email_address), true)
            }
            contactViewModel.getBirthDate().value == null -> {
                showToast(getString(R.string.please_select_birth_date), true)
            }
            contactViewModel.getImageUri().value == null -> {
                showToast(getString(R.string.please_select_photo), true)
            }
            else -> {
                val firstName = edt_first_name.text.toString().trim()
                val lastName = edt_last_name.text.toString().trim()
                val phoneNumber = edt_phone_number.text.toString().trim()
                val emailId = edt_email_address.text.toString().trim()
                val imageExtension: String = Utils(this)
                    .getFileExtension(contactViewModel.getImageUri().value)

                contactViewModel.saveDataToFirebase(
                    firstName = firstName, lastName = lastName, emailId = emailId,
                    phoneNumber = phoneNumber, imageExtension = imageExtension
                ).observe(this, Observer { status ->

                    // check KEYS --> FIREBASE_STATUS_STARTED, FIREBASE_COMPLETED_SAVING_CONTACT,
                    //  FIREBASE_FAILED_SAVING_CONTACT

                    when (status) {
                        FIREBASE_STATUS_STARTED -> {
                            pb_contact_progressbar.visibility = View.VISIBLE
                            disableViews()
                            Log.e(TAG, "Saving contact to firebase Database started")
                        }
                        FIREBASE_COMPLETED_SAVING_CONTACT -> {
                            pb_contact_progressbar.visibility = View.GONE
                            enableViews()
                            Log.e(TAG, "Saving contact to firebase Database Successful")
                            finish()
                        }
                        FIREBASE_FAILED_SAVING_CONTACT -> {
                            pb_contact_progressbar.visibility = View.GONE
                            enableViews()
                            Log.e(TAG, "Saving contact to firebase Database Failed")
                            showToast("Saving contact to firebase Database Failed", true)
                            finish()
                        }
                        else -> {
                            pb_contact_progressbar.visibility = View.GONE
                            enableViews()
                            Log.e(TAG, "Something went Wrong!")
                            showToast("Something went Wrong!", true)
                            finish()
                        }
                    }
                })
            }
        }
    }
}
