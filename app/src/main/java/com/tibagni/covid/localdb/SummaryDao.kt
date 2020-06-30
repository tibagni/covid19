package com.tibagni.covid.localdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.tibagni.covid.summary.Summary

@Dao
interface SummaryDao {
    @Insert(onConflict = REPLACE)
    fun save(summary: Summary)

    @Query("SELECT * FROM summary LIMIT 1")
    fun load(): LiveData<Summary?>

    @Query("SELECT * FROM summary LIMIT 1")
    fun get(): Summary?
}