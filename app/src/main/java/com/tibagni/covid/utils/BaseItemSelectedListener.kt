package com.tibagni.covid.utils

import android.view.View
import android.widget.AdapterView

open class BaseItemSelectedListener: AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>?) {}
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectionChanged()
    }
    open fun selectionChanged() {}
}