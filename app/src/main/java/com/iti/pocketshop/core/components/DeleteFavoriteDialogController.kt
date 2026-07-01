package com.iti.pocketshop.core.components

import com.iti.pocketshop.common.favorites.domain.model.FavoriteProduct
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object DeleteFavoriteDialogController {

    private val _events = Channel<FavoriteProduct>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(product: FavoriteProduct) {
        _events.send(product)
    }
}


