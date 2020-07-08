package com.tibagni.covid.news

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Article(
    val title: String,
    val description: String,
    val image: String?,
    val url: String,
    val source: String,
    val publishDate: Long
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}