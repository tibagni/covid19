package com.tibagni.covid.summary

import android.graphics.drawable.Drawable

data class Top5CountrySummary(
    val countryCode: String,
    val countryName: String,
    val stat: Int
)