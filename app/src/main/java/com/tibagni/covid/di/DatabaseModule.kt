package com.tibagni.covid.di

import android.content.Context
import androidx.room.Room
import com.tibagni.covid.localdb.CountrySummaryDao
import com.tibagni.covid.localdb.Covid19Database
import com.tibagni.covid.localdb.SummaryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): Covid19Database {
        return Room.databaseBuilder(
            context,
            Covid19Database::class.java,
            "local.db"
        ).build()
    }

    @Provides
    fun provideSummaryDao(db: Covid19Database): SummaryDao {
        return db.summaryDao()
    }

    @Provides
    fun provideCountrySummaryDao(db: Covid19Database): CountrySummaryDao {
        return db.countrySummaryDao()
    }
}