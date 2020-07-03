package com.tibagni.covid.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.tibagni.covid.api.Covid19API
import com.tibagni.covid.countries.CountrySummary
import com.tibagni.covid.di.IoExecutor
import com.tibagni.covid.di.MainExecutor
import com.tibagni.covid.localdb.CountrySummaryDao
import com.tibagni.covid.localdb.SummaryDao
import com.tibagni.covid.summary.Summary
import com.tibagni.covid.utils.MutableSingleEventLiveData
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Covid19Repository @Inject constructor(
    private val api: Covid19API,
    private val summaryDao: SummaryDao,
    private val countrySummaryDao: CountrySummaryDao,
    @IoExecutor private val executor: Executor,
    @MainExecutor private val uiExecutor: Executor
) {
    val summaryLoadingStatus: LiveData<LoadingStatus> get() = _summaryLoadingStatus
    private val _summaryLoadingStatus = MutableSingleEventLiveData<LoadingStatus>()

    fun getSummary(): LiveData<Summary?> {
        refreshSummary(false)
        return summaryDao.load()
    }

    fun getCountriesSummary(): LiveData<List<CountrySummary>> {
        refreshSummary(false)
        return countrySummaryDao.loadAll()
    }

    fun getCountriesSummaryFiltered(filter: String): LiveData<List<CountrySummary>> {
        return countrySummaryDao.loadFiltered("%$filter%")
    }

    fun updateCountrySummary(countrySummary: CountrySummary) {
        executor.execute {
            countrySummaryDao.update(countrySummary)
        }
    }

    fun refreshSummary(forceRefresh: Boolean) {
        executor.execute {
            if (!forceRefresh && !shouldRefreshSummary()) return@execute

            uiExecutor.execute { _summaryLoadingStatus.value = LoadingStatus.loading() }
            try {
                val response = api.getSummary().execute()
                val summaryResponse = response.body()
                summaryResponse?.let { summaryDao.save(it.toSummary()) }
                summaryResponse?.let { countrySummaryDao.saveAll(it.toCountrySummaryList()) }
                uiExecutor.execute {
                    _summaryLoadingStatus.value = LoadingStatus.success()
                }
            } catch (exception: Exception) {
                Log.w("Covid19Repository", "Failed to fetch from API", exception)
                uiExecutor.execute {
                    _summaryLoadingStatus.value = LoadingStatus.fail(exception.message)
                }
            }
        }
    }

    private fun shouldRefreshSummary(): Boolean {
        val currentSummary = summaryDao.get()
        if (currentSummary != null) {
            val hoursSinceLastUpdate = TimeUnit.MILLISECONDS.toHours(
                System.currentTimeMillis() - currentSummary.updatedAt
            )
            return hoursSinceLastUpdate > 12
        }
        return true;
    }
}