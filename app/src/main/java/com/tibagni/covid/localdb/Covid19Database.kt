package com.tibagni.covid.localdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tibagni.covid.summary.Summary

@Database(entities = [Summary::class], version = 1, exportSchema = false)
abstract class Covid19Database: RoomDatabase() {
    abstract fun summaryDao(): SummaryDao
}