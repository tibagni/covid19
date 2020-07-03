package com.tibagni.covid.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
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

fun ViewGroup.enableChildren(enable: Boolean) {
    for (i in 0 until this.childCount) {
        this[i].isEnabled = enable
    }
}