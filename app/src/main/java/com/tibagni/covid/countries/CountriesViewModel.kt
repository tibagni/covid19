package com.tibagni.covid.countries

import android.text.TextUtils
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.tibagni.covid.repository.Covid19Repository
import dagger.hilt.android.scopes.ActivityRetainedScoped

@ActivityRetainedScoped
class CountriesViewModel @ViewModelInject constructor(
    private val repository: Covid19Repository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val filter = MutableLiveData<String>()

    private val countriesSummary = Transformations.switchMap(filter) {
        if (TextUtils.isEmpty(it)) {
            repository.getCountriesSummary()
        } else {
            repository.getCountriesSummaryFiltered(it)
        }
    }
    val sortedCountriesSummary = Transformations.map(countriesSummary) {
        _sortingState.apply(it)
    }

    val loadingStatus = repository.summaryLoadingStatus

    private var _sortingState: CountrySummarySortingState = CountrySummarySortingState.EMPTY
    var sortingState: CountrySummarySortingState
        get() = _sortingState
        set(value) {
            _sortingState = value
            filter.value = filter.value // Just to trigger a new query
        }

    init {
        filter.value = ""
    }

    fun updateCountrySummary(countrySummary: CountrySummary) {
        repository.updateCountrySummary(countrySummary)
    }

    fun refresh(forceRefresh: Boolean) {
        repository.refreshSummary(forceRefresh)
    }

    fun filter(filterText: String) {
        filter.value = filterText
    }
}