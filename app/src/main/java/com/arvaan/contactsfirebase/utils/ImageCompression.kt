package com.arvaan.contactsfirebase.utils

import android.content.Context
import android.graphics.*
import android.os.AsyncTask
import android.os.Environment
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.roundToInt

open class ImageCompression(private val context: Context) :
    AsyncTask<String?, Void?, String?>() {


    override fun onPostExecute(imagePath: String?) {
        // imagePath is path of new compressed image.
    }

    private fun compressImage(imagePath: String?): String {
        var scaledBitmap: Bitmap? = null
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(imagePath, options)
        var actualHeight = options.outHeight
        var actualWidth = options.outWidth
        var imgRatio = actualWidth.toFloat() / actualHeight.toFloat()
        val maxRatio =
            maxWidth / maxHeight
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            when {
                imgRatio < maxRatio -> {
                    imgRatio = maxHeight / actualHeight
                    actualWidth = (imgRatio * actualWidth).toInt()
                    actualHeight = maxHeight.toInt()
                }
                imgRatio > maxRatio -> {
                    imgRatio = maxWidth / actualWidth
                    actualHeight = (imgRatio * actualHeight).toInt()
                    actualWidth = maxWidth.toInt()
                }
                else -> {
                    actualHeight = maxHeight.toInt()
                    actualWidth = maxWidth.toInt()
                }
            }
        }
        options.inSampleSize =
            calculateInSampleSize(options, actualWidth, actualHeight)
        options.inJustDecodeBounds = false
        options.inDither = false
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)
        try {
            bmp = BitmapFactory.decodeFile(imagePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap!!)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bmp!!,
            middleX - bmp.width / 2,
            middleY - bmp.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )
        bmp.recycle()
        val exif: ExifInterface
        try {
            exif = ExifInterface(imagePath!!)
            val orientation =
                exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            val matrix = Matrix()
            when (orientation) {
                6 -> {
                    matrix.postRotate(90f)
                }
                3 -> {
                    matrix.postRotate(180f)
                }
                8 -> {
                    matrix.postRotate(270f)
                }
            }
            scaledBitmap = Bitmap.createBitmap(
                scaledBitmap,
                0,
                0,
                scaledBitmap.width,
                scaledBitmap.height,
                matrix,
                true
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val out: FileOutputStream?
        val filepath = filename
        try {
            out = FileOutputStream(filepath)

            //write the compressed bitmap at the destination specified by filename.
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return filepath
    }

    // Create the storage directory if it does not exist
    private val filename: String
        get() {
            val mediaStorageDir = File(
                Environment.getExternalStorageDirectory()
                    .toString() + "/Android/data/"
                        + context.applicationContext.packageName
                        + "/Files/Compressed"
            )

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                mediaStorageDir.mkdirs()
            }
            val mImageName =
                "IMG_" + System.currentTimeMillis().toString() + ".jpg"
            return mediaStorageDir.absolutePath + "/" + mImageName
        }

    companion object {
        private const val maxHeight = 1280.0f
        private const val maxWidth = 1280.0f
        private fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int
        ): Int {
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1
            if (height > reqHeight || width > reqWidth) {
                val heightRatio =
                    (height.toFloat() / reqHeight.toFloat()).roundToInt()
                val widthRatio =
                    (width.toFloat() / reqWidth.toFloat()).roundToInt()
                inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
            }
            val totalPixels = width * height.toFloat()
            val totalReqPixelsCap = reqWidth * reqHeight * 2.toFloat()
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++
            }
            return inSampleSize
        }
    }

    override fun doInBackground(vararg strings: String?): String? {
        return if (strings.isEmpty() || strings[0] == null) null
        else compressImage(
            strings[0]
        )
    }

}