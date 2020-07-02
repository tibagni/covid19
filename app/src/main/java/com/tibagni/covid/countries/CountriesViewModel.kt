package com.tibagni.covid.countries

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tibagni.covid.repository.Covid19Repository
import dagger.hilt.android.scopes.ActivityRetainedScoped

@ActivityRetainedScoped
class CountriesViewModel @ViewModelInject constructor(
    private val repository: Covid19Repository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val loadingStatus = repository.summaryLoadingStatus
    val countriesSummary = repository.getCountriesSummary()

    fun refresh() {
        repository.refreshSummary(true)
    }
}