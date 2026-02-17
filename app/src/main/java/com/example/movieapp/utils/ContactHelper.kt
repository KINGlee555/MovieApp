package com.example.movieapp.utils

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import com.example.movieapp.data.models.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactHelper {

    suspend fun getContacts(context: Context) : List<Contact> {

        return withContext(Dispatchers.IO) {
            val cursor  = getContactCursor(context)
            val contacts = resolveContactDataFromCursor(cursor)

            cursor?.close()

            contacts
        }
    }

    private fun getContactCursor(context: Context) : Cursor? {

        return context.contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            null,
            ContactsContract.Data.HAS_PHONE_NUMBER + "!=0 AND(" + ContactsContract.Data.MIMETYPE + "=?)",
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
            ),
        ContactsContract.Data.DISPLAY_NAME



        )
    }

    private fun resolveContactDataFromCursor(cursor: Cursor?) : List<Contact> {

        val contacts  = arrayListOf<Contact>()

        if(cursor != null && cursor.count > 0 && cursor.moveToFirst()) {

            do {

                val contactsId =
                    cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Data.CONTACT_ID))

                val contactName =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME_PRIMARY))

                val data1 =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data.DATA1))

                contacts.addContact(contactsId,contactName,data1)

            } while (cursor.moveToNext())
        }
        return contacts
    }

    private fun ArrayList<Contact>.addContact(
        contactId:Int,
        contactName:String,
        data1:String?
    ) {
        if(data1 != null && this.none { it.id == contactId }) {

            val contact = Contact()
            contact.id = contactId
            contact.name = contactName
            contact.phoneNumber = data1

            add(contact)
        }
    }
}