package com.example.picodiploma.storyapp.paginglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.picodiploma.storyapp.api.response.Story
import kotlinx.coroutines.flow.Flow

class PagingListViewModel(private val repository: PagingListRepository) : ViewModel() {

    private var currentStoryFlow: Flow<PagingData<Story>>? = null

    fun getStories(): Flow<PagingData<Story>> {
        val lastResult = currentStoryFlow
        if (lastResult != null) {
            return lastResult
        }
        val newResult = repository.getStories()
            .cachedIn(viewModelScope)
        currentStoryFlow = newResult
        return newResult
    }
}




