package com.iti.pocketshop.core.sessionmanager.data.datasource.local

import com.iti.pocketshop.core.sessionmanager.domain.model.CustomerSession

interface CustomerTokenStore {
    suspend fun read(): CustomerSession?
    suspend fun save(session: CustomerSession)
    suspend fun clear()
}