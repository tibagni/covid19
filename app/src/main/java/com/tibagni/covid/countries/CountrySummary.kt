package com.tibagni.covid.countries

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CountrySummary(
    @PrimaryKey val countryCode: String,
    val countryName: String,
    val newConfirmed: Int,
    val totalConfirmed: Int,
    val newDeaths: Int,
    val totalDeaths: Int,
    val newRecovered: Int,
    val totalRecovered: Int,
    val updatedAt: Long
)