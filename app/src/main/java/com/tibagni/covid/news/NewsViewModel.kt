package com.tibagni.covid.news

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.tibagni.covid.repository.NewsRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped

@ActivityRetainedScoped
class NewsViewModel @ViewModelInject constructor(val repository: NewsRepository) : ViewModel() {
    val loadingStatus = repository.newsLoadingStatus
    val articles = repository.getNews()

    fun refresh(forceRefresh: Boolean) {
        repository.refreshNews(forceRefresh)
    }
}