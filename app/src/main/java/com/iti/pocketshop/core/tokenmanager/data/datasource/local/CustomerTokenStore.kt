package com.iti.pocketshop.core.tokenmanager.data.datasource.local

import com.iti.pocketshop.core.tokenmanager.domain.model.CustomerSession

interface CustomerTokenStore {
    fun read(): CustomerSession?

    fun save(session: CustomerSession)
    
    fun clear()
}