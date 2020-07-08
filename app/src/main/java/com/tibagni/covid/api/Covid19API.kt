package com.tibagni.covid.api

import retrofit2.Call
import retrofit2.http.GET

interface Covid19API {

    @GET("/summary")
    fun getSummary(): Call<SummaryAPIResponse>
}