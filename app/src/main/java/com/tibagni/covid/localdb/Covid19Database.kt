package com.tibagni.covid.localdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tibagni.covid.countries.CountrySummary
import com.tibagni.covid.news.Article
import com.tibagni.covid.news.NewsMeta
import com.tibagni.covid.summary.Summary

@Database(
    entities = [
        Summary::class,
        CountrySummary::class,
        Article::class,
        NewsMeta::class
    ], version = 1, exportSchema = false
)
abstract class Covid19Database : RoomDatabase() {
    abstract fun summaryDao(): SummaryDao
    abstract fun countrySummaryDao(): CountrySummaryDao
    abstract fun newsDao(): NewsDao
    abstract fun newsMetaDao(): NewsMetaDao
}