package com.tibagni.covid.api

import com.google.gson.annotations.SerializedName

data class SummaryAPIResponse(
    @field:SerializedName("Global") val global: SummaryInfo,
    @field:SerializedName("Countries") val countries: List<CountryInfo>,
    @field:SerializedName("Date") val date: String
) {
    data class SummaryInfo(
        @field:SerializedName("NewConfirmed") val newConfirmed: Int,
        @field:SerializedName("TotalConfirmed") val totalConfirmed: Int,
        @field:SerializedName("NewDeaths") val newDeaths: Int,
        @field:SerializedName("TotalDeaths") val totalDeaths: Int,
        @field:SerializedName("NewRecovered") val newRecovered: Int,
        @field:SerializedName("TotalRecovered") val totalRecovered: Int
    )

    data class CountryInfo(
        @field:SerializedName("Country") val country: String,
        @field:SerializedName("CountryCode") val countryCode: String,
        @field:SerializedName("Slug") val slug: String,
        @field:SerializedName("NewConfirmed") val newConfirmed: Int,
        @field:SerializedName("TotalConfirmed") val totalConfirmed: Int,
        @field:SerializedName("NewDeaths") val newDeaths: Int,
        @field:SerializedName("TotalDeaths") val totalDeaths: Int,
        @field:SerializedName("NewRecovered") val newRecovered: Int,
        @field:SerializedName("TotalRecovered") val totalRecovered: Int,
        @field:SerializedName("Date") val date: String
    )
}