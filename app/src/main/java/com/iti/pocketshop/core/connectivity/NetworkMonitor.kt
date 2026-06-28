package com.iti.pocketshop.core.connectivity

import kotlinx.coroutines.flow.Flow


interface NetworkMonitor {
    val isOnline: Flow<Boolean>
}