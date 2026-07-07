package com.iti.pocketshop.features.orders.domain

import java.time.Instant

enum class OrderTimelinePhase {
    ORDERED,
    PROCESSING,
    DELIVERED,
}


object OrderTimeline {
    private const val DAY_MILLIS = 86_400_000L
    private const val PROCESSING_START_DAY = 2
    private const val DELIVERY_DAY = 5

    fun deliveryDateEpochMillis(processedAtEpochMillis: Long): Long =
        processedAtEpochMillis + (DELIVERY_DAY - 1) * DAY_MILLIS

    fun phaseFor(processedAtEpochMillis: Long, nowEpochMillis: Long): OrderTimelinePhase {
        if (processedAtEpochMillis <= 0L) return OrderTimelinePhase.ORDERED
        val dayNumber = (nowEpochMillis - processedAtEpochMillis) / DAY_MILLIS + 1
        return when {
            dayNumber < PROCESSING_START_DAY -> OrderTimelinePhase.ORDERED
            dayNumber < DELIVERY_DAY -> OrderTimelinePhase.PROCESSING
            else -> OrderTimelinePhase.DELIVERED
        }
    }
}

fun String.toEpochMillisOrZero(): Long =
    runCatching { Instant.parse(this).toEpochMilli() }.getOrDefault(0L)
