package com.iti.pocketshop.core.components

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object SignInDialogController {

    private val _events = Channel<Boolean>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(open: Boolean) {
        _events.send(open)
    }
}


