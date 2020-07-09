package com.tibagni.covid.view

class Expandable <T> (val content: T) {
    var isExpanded: Boolean = false

    fun toggle() {
        isExpanded = !isExpanded
    }
}