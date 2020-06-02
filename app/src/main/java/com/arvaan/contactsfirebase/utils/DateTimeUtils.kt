package com.arvaan.contactsfirebase.utils

import java.text.SimpleDateFormat
import java.util.*

class DateTimeUtils {

    fun toDoubleDigit(value: Int): String {
        return if (value < 10) {
            "0$value"
        } else {
            value.toString()
        }
    }

    fun getBirthDateString(value: String): String {
        val date: Date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(value) as Date
        return SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(date)
    }
}