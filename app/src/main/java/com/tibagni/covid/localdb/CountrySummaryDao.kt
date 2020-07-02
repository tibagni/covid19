package com.tibagni.covid.localdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.tibagni.covid.countries.CountrySummary

@Dao
interface CountrySummaryDao {
    @Insert(onConflict = REPLACE)
    @JvmSuppressWildcards
    fun saveAll(countrySummaryList: List<CountrySummary>)

    @Query("SELECT * FROM countrysummary")
    fun loadAll(): LiveData<List<CountrySummary>>
}