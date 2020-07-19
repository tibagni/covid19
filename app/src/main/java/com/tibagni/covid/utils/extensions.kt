package com.tibagni.covid.utils

import android.content.Context
import android.os.Build
import java.lang.Exception
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun Int.format(context: Context?): String {
    val nf = if (context != null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NumberFormat.getInstance(context.resources.configuration.locales[0])
        } else {
            NumberFormat.getInstance(context.resources.configuration.locale)
        }
    } else {
        NumberFormat.getInstance()
    }
    return nf.format(this)
}

fun Long.formatDate(): String = SimpleDateFormat().format(Date(this))

fun String.toMillis(dateFormat: String): Long {
    return try {
        val formatter = SimpleDateFormat(dateFormat)
        val date = formatter.parse(this)
        date.time
    } catch (e: Exception) {
        0L
    }
}

fun String?.or(defaultStr: String): String {
    return if (this.isNullOrBlank()) defaultStr else this
}