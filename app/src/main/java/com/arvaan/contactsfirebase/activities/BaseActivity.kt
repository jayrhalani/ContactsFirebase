package com.arvaan.contactsfirebase.activities

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Patterns
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arvaan.contactsfirebase.R

@SuppressLint("Registered")
open class BaseActivity(contentLayoutId: Int) : AppCompatActivity(contentLayoutId) {

    companion object {
        const val REQUEST_CODE_PERMISSIONS = 101
        const val KEY_PERMISSIONS_REQUEST_COUNT = "KEY_PERMISSIONS_REQUEST_COUNT"
        const val MAX_NUMBER_REQUEST_PERMISSIONS = 2
    }

    var permissionRequestCount: Int = 0

    fun showToast(message: String, isShort: Boolean) {
        if (isShort) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    // TODO -> Move this to ViewModel
    fun isEditTextEmpty(editText: EditText): Boolean {
        return editText.text.toString().trim().isEmpty()
    }

    // TODO -> Move this to ViewModel
    fun isEditTextValueUpdated(editText: EditText, previousValue: String): Boolean {
        return editText.text.toString() != previousValue
    }

    fun isValidPhoneNumber(editText: EditText): Boolean {
        return editText.text.toString().trim().length == 10
    }

    fun isValidEmailId(editText: EditText): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(editText.text.toString()).matches()
    }

    fun disableViews() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    fun enableViews() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    fun requestPermissionsIfNecessary(permissions: Array<String>) {
        if (!checkAllPermissions(permissions)) {
            if (permissionRequestCount < MAX_NUMBER_REQUEST_PERMISSIONS) {
                permissionRequestCount += 1
                ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    REQUEST_CODE_PERMISSIONS
                )
            } else {
                Toast.makeText(
                    this,
                    R.string.set_permissions_in_settings,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun checkAllPermissions(permissions: Array<String>): Boolean {
        var hasPermissions = true
        for (permission in permissions) {
            hasPermissions = hasPermissions and isPermissionGranted(permission)
        }
        return hasPermissions
    }

    private fun isPermissionGranted(permission: String) =
        ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED
}