package com.arvaan.contactsfirebase.activities.display_contact_activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.arvaan.contactsfirebase.R
import com.arvaan.contactsfirebase.activities.BaseActivity
import com.arvaan.contactsfirebase.activities.contact_activity.ContactActivity
import com.arvaan.contactsfirebase.utils.*
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_display_contact.*

class DisplayContactActivity : BaseActivity(R.layout.activity_display_contact) {

    companion object {
        private val TAG by lazy { DisplayContactActivity::class.java.simpleName }
    }

    private lateinit var displayContactViewModel: DisplayContactViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
        initViews()
    }

    private fun initViews() {
        setupViewModelData()
        setupIntentData()
    }

    private fun setupViewModelData() {
        displayContactViewModel = ViewModelProvider(this).get(DisplayContactViewModel::class.java)
        displayContactViewModel.getFullName().observe(this, Observer { fullName ->
            title = fullName
        })

        displayContactViewModel.getImageUrl().observe(this, Observer { imageUrl ->
            Glide.with(this)
                .load(imageUrl)
                .into(iv_display_contact_avatar)
            //  Blue Effect
            //        Glide.with(this)
            //            .load(imageUrl)
            //            .transform(BlurTransformation(this))
            //            .into(iv_display_contact_avatar)
        })

        displayContactViewModel.getEmailAddress().observe(this, Observer { emailAddress ->
            tv_display_contact_email_id.text = emailAddress
        })

        displayContactViewModel.getPhoneNumber().observe(this, Observer { phoneNumber ->
            tv_display_contact_phone_number.text = phoneNumber
        })

        displayContactViewModel.getBirthDate().observe(this, Observer { birthDate ->
            tv_display_contact_birth_date.text = birthDate
        })
    }

    private fun setupIntentData() {
        val intent = intent

        if (intent.hasExtra(KEY_DATABASE_ITEM_KEY)) {
            displayContactViewModel.getFirebaseDatabaseKey().value =
                intent.getStringExtra(KEY_DATABASE_ITEM_KEY)
        }

        if (intent.hasExtra(KEY_FIRST_NAME) && intent.hasExtra(KEY_LAST_NAME)) {
            val firstName = intent.getStringExtra(KEY_FIRST_NAME)
            val lastName = intent.getStringExtra(KEY_LAST_NAME)
            displayContactViewModel.getFirstName().value = firstName
            displayContactViewModel.getLastName().value = lastName
            displayContactViewModel.getFullName().value = "$firstName $lastName"

        } else if (intent.hasExtra(KEY_FIRST_NAME)) {
            displayContactViewModel.getFirstName().value = intent.getStringExtra(KEY_FIRST_NAME)
        } else if (intent.hasExtra(KEY_LAST_NAME)) {
            displayContactViewModel.getLastName().value = intent.getStringExtra(KEY_LAST_NAME)
        }

        if (intent.hasExtra(KEY_EMAIL_ID)) {
            displayContactViewModel.getEmailAddress().value = intent.getStringExtra(KEY_EMAIL_ID)
        }

        if (intent.hasExtra(KEY_PHONE_NUMBER)) {
            displayContactViewModel.getPhoneNumber().value = intent.getStringExtra(KEY_PHONE_NUMBER)
        }

        if (intent.hasExtra(KEY_BIRTH_DATE)) {
            displayContactViewModel.getBirthDate().value = intent.getStringExtra(KEY_BIRTH_DATE)
        }

        if (intent.hasExtra(KEY_IMAGE_URL)) {
            displayContactViewModel.getImageUrl().value = intent.getStringExtra(KEY_IMAGE_URL)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.item_display_contact_edit -> {
                editContact()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun editContact() {
        val editIntent = Intent(this@DisplayContactActivity, ContactActivity::class.java)
        editIntent.putExtra(KEY_INTENT_SELECTION, KEY_INTENT_UPDATE)
        editIntent.putExtra(
            KEY_DATABASE_ITEM_KEY,
            displayContactViewModel.getFirebaseDatabaseKey().value
        )
        editIntent.putExtra(KEY_FIRST_NAME, displayContactViewModel.getFirstName().value)
        editIntent.putExtra(KEY_LAST_NAME, displayContactViewModel.getLastName().value)
        editIntent.putExtra(KEY_EMAIL_ID, displayContactViewModel.getEmailAddress().value)
        editIntent.putExtra(KEY_PHONE_NUMBER, displayContactViewModel.getPhoneNumber().value)
        editIntent.putExtra(KEY_BIRTH_DATE, displayContactViewModel.getBirthDate().value)
        editIntent.putExtra(KEY_IMAGE_URL, displayContactViewModel.getImageUrl().value)
        startActivity(editIntent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.display_contact_menu, menu)
        return true
    }


}
