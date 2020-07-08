package com.tibagni.covid.localdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tibagni.covid.news.Article

@Dao
interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    fun saveAll(newsList: List<Article>)

    @Query("SELECT * FROM article")
    fun loadAll(): LiveData<List<Article>>

    @Query("DELETE FROM article")
    fun clear()
}