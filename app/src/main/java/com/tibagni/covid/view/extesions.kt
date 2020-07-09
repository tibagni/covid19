package com.tibagni.covid.view

import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.core.view.get

fun ViewGroup.enableChildren(enable: Boolean) {
    for (i in 0 until this.childCount) {
        this[i].isEnabled = enable
    }
}

fun Spinner.onSelectionChanged(callback: () -> Unit) {
    this.onItemSelectedListener = object :
        AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) { }
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            callback()
        }
    }
}