package com.tibagni.covid.utils

class Expandable <T> (val content: T) {
    var isExpanded: Boolean = false

    fun toggle() {
        isExpanded = !isExpanded
    }
}