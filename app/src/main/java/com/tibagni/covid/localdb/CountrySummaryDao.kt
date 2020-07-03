package com.tibagni.covid.localdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.tibagni.covid.countries.CountrySummary

@Dao
interface CountrySummaryDao {
    @Insert(onConflict = REPLACE)
    @JvmSuppressWildcards
    fun saveAll(countrySummaryList: List<CountrySummary>)

    @Query("SELECT * FROM countrysummary ORDER BY isPinned DESC")
    fun loadAll(): LiveData<List<CountrySummary>>

    @Query("SELECT * FROM countrysummary WHERE countryName LIKE :filter ORDER BY isPinned DESC")
    fun loadFiltered(filter: String): LiveData<List<CountrySummary>>

    @Update
    fun update(countrySummary: CountrySummary)
}