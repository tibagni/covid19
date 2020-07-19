package com.tibagni.covid.summary

data class Top5CountrySummary(
    val countryCode: String,
    val countryName: String,
    val stat: Int
)