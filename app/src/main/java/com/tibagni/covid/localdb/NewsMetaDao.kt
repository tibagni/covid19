package com.tibagni.covid.localdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tibagni.covid.news.NewsMeta

@Dao
interface NewsMetaDao {
    @Query("DELETE FROM newsMeta")
    fun clear()

    @Query("SELECT * FROM newsMeta WHERE `key`=:key")
    fun get(key: String): NewsMeta?

    @Insert
    fun put(meta: NewsMeta)
}