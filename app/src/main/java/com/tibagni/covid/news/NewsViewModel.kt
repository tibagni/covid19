package com.tibagni.covid.news

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.tibagni.covid.repository.NewsRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped

@ActivityRetainedScoped
class NewsViewModel @ViewModelInject constructor(repository: NewsRepository) : ViewModel() {
    val loadingStatus = repository.newsLoadingStatus
    val articles = repository.getNews()
}