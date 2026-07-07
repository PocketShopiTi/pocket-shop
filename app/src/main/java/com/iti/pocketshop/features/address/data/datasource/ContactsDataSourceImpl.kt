package com.iti.pocketshop.features.address.data.datasource

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import com.iti.pocketshop.features.address.domain.model.ContactInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContactsDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : ContactsDataSource {

    override suspend fun getContact(uri: Uri): ContactInfo? = withContext(Dispatchers.IO) {
        context.contentResolver.query(
            uri,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ),
            null,
            null,
            null,
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                ContactInfo(
                    displayName = if (nameIndex >= 0) cursor.getString(nameIndex) else null,
                    phoneNumber = if (numberIndex >= 0) cursor.getString(numberIndex) else null,
                )
            } else {
                null
            }
        }
    }
}