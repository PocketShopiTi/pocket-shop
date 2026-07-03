package com.iti.pocketshop.common.sessionmanager.data.datasource.local

import com.iti.pocketshop.common.sessionmanager.domain.model.CustomerSession

interface CustomerTokenStore {
    suspend fun read(): CustomerSession?
    suspend fun save(session: CustomerSession)
    suspend fun clear()
}