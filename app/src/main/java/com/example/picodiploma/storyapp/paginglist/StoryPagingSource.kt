package com.example.picodiploma.storyapp.paginglist

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.picodiploma.storyapp.api.ApiServiceHelper
import com.example.picodiploma.storyapp.api.response.Story

class StoryPagingSource(private val apiServiceHelper: ApiServiceHelper) : PagingSource<Int, Story>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize

            val storyList = apiServiceHelper.getStoryList(page, pageSize)

            LoadResult.Page(
                data = storyList,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (storyList.isNotEmpty()) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        // Use the first page as the refresh key
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}
