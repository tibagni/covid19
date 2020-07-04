package com.tibagni.covid.repository

import com.tibagni.covid.api.SummaryAPIResponse
import com.tibagni.covid.countries.CountrySummary
import com.tibagni.covid.summary.Summary

/**
 * Extension to convert from Retrofit to Room types
 */
fun SummaryAPIResponse.toSummary(): Summary {
    return Summary(
        0, // Always use 0 as ID - We only want to save the most recent summary
        this.global.newConfirmed,
        this.global.totalConfirmed,
        this.global.newDeaths,
        this.global.totalDeaths,
        this.global.newRecovered,
        this.global.totalRecovered,
        System.currentTimeMillis()
    )
}

fun SummaryAPIResponse.toCountrySummaryList(pinned: List<CountrySummary>): List<CountrySummary> {
    val now = System.currentTimeMillis()
    val pinnedSet = pinned.map { it.countryCode }.toSet()
    return this.countries.map {
        CountrySummary(
            it.countryCode,
            it.country,
            it.newConfirmed,
            it.totalConfirmed,
            it.newDeaths,
            it.totalDeaths,
            it.newRecovered,
            it.totalRecovered,
            now,
            pinnedSet.contains(it.countryCode)
        )
    }
}