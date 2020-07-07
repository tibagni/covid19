package com.tibagni.covid.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
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

fun Spinner.onSelectionChanged(callback: () -> Unit) {
    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) { }
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            callback()
        }
    }
}