package com.example.picodiploma.storyapp.paginglist

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.picodiploma.storyapp.api.ApiServiceHelper
import com.example.picodiploma.storyapp.api.response.Story
import kotlinx.coroutines.flow.Flow

class PagingListRepository(private val apiServiceHelper: ApiServiceHelper) {

    fun getStories(): Flow<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoryPagingSource(apiServiceHelper) }
        ).flow
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
