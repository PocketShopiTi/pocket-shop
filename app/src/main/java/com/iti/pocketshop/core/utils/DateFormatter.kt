package com.iti.pocketshop.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toFormattedDate(pattern: String = "dd MMM yyyy"): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(Date(this))
