package com.tibagni.covid.di

import com.tibagni.covid.repository.ResourceData
import com.tibagni.covid.utils.AndroidResourceData
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
interface AndroidModule {
    @Singleton
    @Binds
    fun provideAndroidResourceData(resData: AndroidResourceData): ResourceData;
}