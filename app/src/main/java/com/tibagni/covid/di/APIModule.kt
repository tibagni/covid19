package com.tibagni.covid.di

import com.tibagni.covid.BuildConfig
import com.tibagni.covid.api.Covid19API
import com.tibagni.covid.api.NewsAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object APIModule {
    @Singleton
    @Provides
    fun provideCovid19API(): Covid19API {
        return Retrofit.Builder()
            .baseUrl("https://api.covid19api.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Covid19API::class.java)
    }

    @Singleton
    @Provides
    fun provideNewsAPI(): NewsAPI {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.NEWS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsAPI::class.java)
    }
}