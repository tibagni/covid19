package com.tibagni.covid.api

import com.google.gson.annotations.SerializedName

data class NewsHeadlinesAPIResponse(
    @field:SerializedName("timestamp") val timestamp: Long,
    @field:SerializedName("articleCount") val articleCount: Int,
    @field:SerializedName("articles") val articles: List<Article>
) {

    data class Article(
        @field:SerializedName("title") val title: String,
        @field:SerializedName("description") val description: String,
        @field:SerializedName("url") val url: String,
        @field:SerializedName("image") val image: String,
        @field:SerializedName("publishedAt") val publishedAt: String,
        @field:SerializedName("source") val source: Source
    )

    data class Source(
        @field:SerializedName("name") val name: String,
        @field:SerializedName("url") val url: String
    )
}