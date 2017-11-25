package org.de_studio.recentappswitcher.android

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.provider.MediaStore
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.widget.ImageView
import android.widget.Toast
import org.de_studio.recentappswitcher.R
import java.io.IOException



/**
 * Created by HaiNguyen on 11/25/17.
 */
object ContactPhotoLoader {
    fun loadContactPhoto(contactId: Long, icon: ImageView, context: Context) {
        val person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId)
        val photo = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY)
        if (photo != null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, photo)
                val drawable = RoundedBitmapDrawableFactory.create(context.resources, bitmap)
                drawable.isCircular = true
                icon.setImageDrawable(drawable)
                icon.colorFilter = null
            } catch (e: IOException) {
                e.printStackTrace()
                icon.setImageResource(R.drawable.ic_contact_default)
            } catch (e: SecurityException) {
                Toast.makeText(context, context.getString(R.string.missing_contact_permission), Toast.LENGTH_LONG).show()
            }

        } else {
            icon.setImageResource(R.drawable.ic_contact_default)
        }
    }

    fun loadContactPhotoNew(contactId: Long, icon: ImageView, context: Context) {
        val uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId)
        val input = ContactsContract.Contacts.openContactPhotoInputStream(context.contentResolver, uri)
        if (input != null) {
            val drawable = RoundedBitmapDrawableFactory.create(context.resources, input)
            drawable.isCircular = true
            icon.setImageDrawable(drawable)
            icon.colorFilter = null
        } else {
            icon.setImageResource(R.drawable.ic_contact_default)
        }
    }
}