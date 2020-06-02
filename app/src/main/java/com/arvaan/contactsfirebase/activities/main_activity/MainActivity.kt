package com.arvaan.contactsfirebase.activities.main_activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.arvaan.contactsfirebase.R
import com.arvaan.contactsfirebase.activities.BaseActivity
import com.arvaan.contactsfirebase.activities.contact_activity.ContactActivity
import com.arvaan.contactsfirebase.activities.display_contact_activity.DisplayContactActivity
import com.arvaan.contactsfirebase.adapters.ContactAdapter
import com.arvaan.contactsfirebase.data.Contact
import com.arvaan.contactsfirebase.utils.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(R.layout.activity_main), ContactAdapter.OnItemClickListener {

    companion object {
        private val TAG by lazy { MainActivity::class.java.simpleName }
    }

    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
    }

    private fun initViews() {
        setUpViewModelAndObserve()

        adapter = ContactAdapter(this@MainActivity, this)
        rv_contacts.layoutManager = LinearLayoutManager(this)
        rv_contacts.setHasFixedSize(true)
        rv_contacts.adapter = adapter

        fab_add_contact.setOnClickListener {
            val addContactIntent = Intent(this@MainActivity, ContactActivity::class.java)
            addContactIntent.putExtra(KEY_INTENT_SELECTION, KEY_INTENT_ADD)
            startActivity(addContactIntent)
        }
    }

    private fun setUpViewModelAndObserve() {
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mainViewModel.getContactList().observe(this, Observer { contactList ->
            adapter.setContacts(contactList)
        })
    }

    override fun onItemClick(contact: Contact) {
        val intent = Intent(this@MainActivity, DisplayContactActivity::class.java)
        intent.putExtra(KEY_DATABASE_ITEM_KEY, contact.key)
        intent.putExtra(KEY_FIRST_NAME, contact.firstName)
        intent.putExtra(KEY_LAST_NAME, contact.lastName)
        intent.putExtra(KEY_EMAIL_ID, contact.emailAddress)
        intent.putExtra(KEY_PHONE_NUMBER, contact.phoneNumber)
        intent.putExtra(KEY_BIRTH_DATE, contact.birthDate)
        intent.putExtra(KEY_IMAGE_URL, contact.avatar)
        startActivity(intent)
    }
}
