package com.tibagni.covid.summary

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.tibagni.covid.countries.CountrySummary
import com.tibagni.covid.countries.CountrySummarySortingState
import com.tibagni.covid.repository.Covid19Repository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import com.tibagni.covid.countries.CountrySummarySortingState.*
import java.lang.IllegalStateException

@ActivityRetainedScoped
class SummaryViewModel @ViewModelInject constructor(
    private val repository: Covid19Repository,
    @Assisted private val savedStateHandle: SavedStateHandle
) :
    ViewModel() {
    private val countrySummarySortingState = MutableLiveData<CountrySummarySortingState>()

    val sortingState get() = countrySummarySortingState.value

    val loadingStatus = repository.summaryLoadingStatus
    val summary = repository.getSummary()

    val topCountries = Transformations.switchMap(countrySummarySortingState) { sortingState ->
        Transformations.map(repository.getCountriesSummary()) {
            sortingState.apply(it).take(5).map { s ->
                val stat = getFilteredStat(s, sortingState)
                Top5CountrySummary(s.countryCode, s.countryName, stat)
            }
        }
    }

    init {
        countrySummarySortingState.value =
            CountrySummarySortingState(SortField.CASES, SortPeriod.LAST_24_HOURS, false)
    }

    private fun getFilteredStat(
        summary: CountrySummary,
        sortingState: CountrySummarySortingState
    ): Int {
        val isTotal = sortingState.period == SortPeriod.ALL
        return when (sortingState.sortBy) {
            SortField.CASES -> if (isTotal) summary.totalConfirmed else summary.newConfirmed
            SortField.DEATHS -> if (isTotal) summary.totalDeaths else summary.newDeaths
            SortField.RECOVERED -> if (isTotal) summary.totalRecovered else summary.newRecovered
            SortField.NAME -> throw IllegalStateException("Should never sort top 5 by name")
        }
    }

    fun refreshSummary() {
        repository.refreshSummary(true)
    }

    fun updateTopCountriesSortingState(sortField: SortField, sortPeriod: SortPeriod) {
        countrySummarySortingState.value = CountrySummarySortingState(sortField, sortPeriod, false)
    }
}