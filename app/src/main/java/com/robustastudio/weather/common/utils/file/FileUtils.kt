package com.robustastudio.weather.common.utils.file

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {

    fun saveImageToStorage(
        bitmap: Bitmap,
        filename: String,
        mimeType: String,
        directory: String = Environment.DIRECTORY_PICTURES,
        mediaContentUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI, context: Context
    ): Boolean {
        try {

            val imageOutStream: OutputStream
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                    put(MediaStore.Images.Media.RELATIVE_PATH, directory)
                }

                context.contentResolver.run {
                    val uri =
                        context.contentResolver.insert(mediaContentUri, values)
                            ?: return false
                    imageOutStream = openOutputStream(uri) ?: return false
                }
            } else {
                val imagePath =
                    Environment.getExternalStoragePublicDirectory(directory).absolutePath
                val image = File(imagePath, filename)
                imageOutStream = FileOutputStream(image)
            }

            imageOutStream.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    const val CREATE_FILE = 1

    fun createFile(pickerInitialUri: Uri, context: Activity, name: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, name)

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker before your app creates the document.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        context.startActivityForResult(intent, CREATE_FILE)
    }

    fun saveBitmap(context: Context?, bitmap: Bitmap, dimension: Int): Observable<File?>? {
        return Observable.defer {
            Observable.create(
                ObservableOnSubscribe { e: ObservableEmitter<File?> ->
                    try {
                        var finalBitmap
                          = Bitmap.createScaledBitmap(
                            bitmap, dimension,
                            dimension, false
                        )
                        val outputFile: File = createImageFile(context!!)!!
                        val outputStream =
                            FileOutputStream(outputFile)
                        finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        outputStream.flush()
                        outputStream.close()
                        e.onNext(outputFile)
                    } catch (ioexception: IOException) {
                        e.onError(ioexception)
                    }
                    e.onComplete()
                })
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(context: Context): File? {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val externalCacheDir = context.externalCacheDir
        val imageFileName = "JPEG_" + timeStamp + "_"
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            externalCacheDir /* directory */
        )
    }


}