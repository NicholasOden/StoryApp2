package com.example.picodiploma.storyapp.paginglist

import com.example.picodiploma.storyapp.api.ApiServiceHelper
import com.example.picodiploma.storyapp.api.response.Story
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.paging.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData

import kotlinx.coroutines.CoroutineScope


open class PagingListRepository(
    private val apiServiceHelper: ApiServiceHelper,
    private val coroutineScope: CoroutineScope
) {

    open fun getStories(): LiveData<PagingData<Story>> {
        val storiesLiveData = MutableLiveData<PagingData<Story>>()
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoryPagingSource(apiServiceHelper) }
        ).flow.cachedIn(coroutineScope).asLiveData().observeForever { pagingData ->
            storiesLiveData.value = pagingData
        }
        return storiesLiveData
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}


