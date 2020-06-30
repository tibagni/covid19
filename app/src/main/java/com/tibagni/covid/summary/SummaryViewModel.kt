package com.tibagni.covid.summary

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tibagni.covid.repository.Covid19Repository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityRetainedScoped
class SummaryViewModel @ViewModelInject constructor(
    private val repository: Covid19Repository,
    @Assisted private val savedStateHandle: SavedStateHandle
) :
    ViewModel() {
    val loadingStatus = repository.summaryLoadingStatus
    val summary = repository.getSummary()

    fun refreshSummary() {
        repository.refreshSummary(true)
    }
}