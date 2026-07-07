package com.iti.pocketshop.features.address.data.datasource

import android.net.Uri
import com.iti.pocketshop.features.address.domain.model.ContactInfo

interface ContactsDataSource {
    suspend fun getContact(uri: Uri): ContactInfo?
}