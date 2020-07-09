package com.tibagni.covid.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.tibagni.covid.api.NewsAPI
import com.tibagni.covid.di.IoExecutor
import com.tibagni.covid.localdb.NewsDao
import com.tibagni.covid.localdb.NewsMetaDao
import com.tibagni.covid.news.Article
import com.tibagni.covid.news.NewsMeta
import com.tibagni.covid.utils.MutableSingleEventLiveData
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
    private val api: NewsAPI,
    private val newsDao: NewsDao,
    private val newsMetaDao: NewsMetaDao,
    @IoExecutor private val executor: Executor
) {
    val newsLoadingStatus: LiveData<LoadingStatus> get() = _newsLoadingStatus
    private val _newsLoadingStatus = MutableSingleEventLiveData<LoadingStatus>()

    companion object {
        private const val META_UPDATE_AT = "lastUpdated"
    }

    fun getNews(): LiveData<List<Article>> {
        refreshNews(false)
        return newsDao.loadAll()
    }

    fun refreshNews(forceRefresh: Boolean) {
        executor.execute {
            if (!forceRefresh && !shouldRefreshNews()) {
                // Avoid fetching data from the API when it is not needed
                return@execute
            }
            _newsLoadingStatus.postValue(LoadingStatus.loading())
            try {
                val response = api.getHeadlines().execute()
                val headlinesResponse = response.body()

                headlinesResponse?.let {
                    if (it.articles.isNotEmpty()) {
                        // We don't want to keep old news, so clear the db before adding the new
                        // data
                        newsDao.clear()
                        newsDao.saveAll(it.toNewsList())
                        newsMetaDao.put(
                            NewsMeta(
                                META_UPDATE_AT,
                                System.currentTimeMillis().toString()
                            )
                        )
                    }
                }
                _newsLoadingStatus.postValue(LoadingStatus.success())
            } catch (exception: Exception) {
                _newsLoadingStatus.postValue(LoadingStatus.fail(exception.message))
                Log.w("NewsRepository", "Failed to fetch from API", exception)
            }
        }
    }

    private fun shouldRefreshNews(): Boolean {
        val meta = newsMetaDao.get(META_UPDATE_AT) ?: return true
        val hoursSinceLastUpdate = TimeUnit.MILLISECONDS.toHours(
            System.currentTimeMillis() - meta.value.toLong()
        )
        return hoursSinceLastUpdate > 12
    }
}