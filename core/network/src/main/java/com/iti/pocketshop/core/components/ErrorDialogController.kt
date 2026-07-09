package com.iti.pocketshop.core.components

import com.iti.pocketshop.core.networkutils.PocketDataError
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object ErrorDialogController {

    private val _events = Channel<PocketDataError>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(error: PocketDataError) {
        _events.send(error)
    }
}


