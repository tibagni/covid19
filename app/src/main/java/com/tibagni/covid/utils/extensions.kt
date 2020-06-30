package com.tibagni.covid.utils

import android.content.Context
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun Int.format(context: Context?): String {
    val nf = if (context != null) {
        NumberFormat.getInstance(context.resources.configuration.locales[0])
    } else {
        NumberFormat.getInstance()
    }
    return nf.format(this)
}

fun Long.formatDate(): String = SimpleDateFormat().format(Date(this))