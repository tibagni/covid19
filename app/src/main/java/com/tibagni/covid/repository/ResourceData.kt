package com.tibagni.covid.repository

interface ResourceData {
    fun getStringData(resId: Int, def: String): String
}