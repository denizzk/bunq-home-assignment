package com.dkarakaya.core.util

import java.text.SimpleDateFormat
import java.util.*

fun dateFormatter(date: String): String {
    val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.getDefault())
    val formatter = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())
    return formatter.format(parser.parse(date))
}

fun shortDateFormatter(date: String): String {
    val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.getDefault())
    val formatter = SimpleDateFormat("dd.MM", Locale.getDefault())
    return formatter.format(parser.parse(date))
}
