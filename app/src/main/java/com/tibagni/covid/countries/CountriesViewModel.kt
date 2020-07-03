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

    val loadingStatus = repository.summaryLoadingStatus
    val countriesSummary = Transformations.switchMap(filter) {
        if (TextUtils.isEmpty(it)) {
            repository.getCountriesSummary()
        } else {
            repository.getCountriesSummaryFiltered(it)
        }
    }

    init {
        filter.value = ""
    }

    fun updateCountrySummary(countrySummary: CountrySummary) {
        repository.updateCountrySummary(countrySummary)
    }

    fun refresh() {
        repository.refreshSummary(true)
    }

    fun filter(filterText: String) {
        filter.value = filterText
    }
}