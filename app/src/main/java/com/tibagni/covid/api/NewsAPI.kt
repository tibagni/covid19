package com.tibagni.covid.api

import retrofit2.Call
import retrofit2.http.GET

interface NewsAPI {

    @GET("/api/news?keywords=covid,coronavirus")
    fun getHeadlines(): Call<NewsHeadlinesAPIResponse>
}