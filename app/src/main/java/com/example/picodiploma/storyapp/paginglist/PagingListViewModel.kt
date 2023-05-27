package com.example.picodiploma.storyapp.paginglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.picodiploma.storyapp.api.response.Story
import kotlinx.coroutines.flow.Flow

class PagingListViewModel(private val repository: PagingListRepository) : ViewModel() {

    fun getStories(): Flow<PagingData<Story>> {
        return repository.getStories()
            .cachedIn(viewModelScope)
    }
}

class ViewModelFactory(private val repository: PagingListRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PagingListViewModel::class.java)) {
            return PagingListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}






