package com.tibagni.covid.di

import com.tibagni.covid.api.Covid19API
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
    fun provideAPI(): Covid19API {
        return Retrofit.Builder()
            .baseUrl("https://api.covid19api.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Covid19API::class.java)
    }
}