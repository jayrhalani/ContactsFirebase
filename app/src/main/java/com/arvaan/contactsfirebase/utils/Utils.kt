package com.arvaan.contactsfirebase.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap


class Utils(private val context: Context) {

    fun getFileExtension(uri: Uri?): String {
        val cR: ContentResolver = context.contentResolver
        val mime = MimeTypeMap.getSingleton()
        val mimeType = mime.getExtensionFromMimeType(cR.getType(uri!!))
        return mimeType!!
    }
}