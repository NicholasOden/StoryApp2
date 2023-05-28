package com.example.picodiploma.storyapp.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.picodiploma.storyapp.data.response.Story
import kotlinx.coroutines.flow.Flow

open class PagingListRepository(private val apiServiceHelper: ApiServiceHelper) {

    open fun getStories(): Flow<PagingData<Story>> {
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
