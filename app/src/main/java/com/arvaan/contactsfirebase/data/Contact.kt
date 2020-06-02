package com.arvaan.contactsfirebase.data

data class Contact(
    val key: String = "",
    val avatar: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val emailAddress: String = "",
    val birthDate: String = ""
)