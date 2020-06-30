package com.tibagni.covid.di

import android.content.Context
import androidx.core.content.ContextCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class IoExecutor

@Qualifier
annotation class CpuExecutor

@Qualifier
annotation class MainExecutor

@InstallIn(ApplicationComponent::class)
@Module
object ExecutorModule {
    @IoExecutor
    @Singleton
    @Provides
    fun provideIoExecutor(): Executor = Executors.newFixedThreadPool(5)

    @CpuExecutor
    @Singleton
    @Provides
    fun provideCpuExecutor(): Executor = Executors.newSingleThreadExecutor()

    @MainExecutor
    @Singleton
    @Provides
    fun provideMainExecutor(@ApplicationContext context: Context): Executor =
        ContextCompat.getMainExecutor(context)
}