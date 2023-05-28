package com.example.picodiploma.storyapp.paginglist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.*
import com.example.picodiploma.storyapp.api.ApiServiceHelper
import com.example.picodiploma.storyapp.api.response.Story
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.junit.Assert.*
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.`when`

import org.mockito.Mockito.`when`

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class PagingListViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    @Test
    fun `test loading story data successfully`() = testScope.runBlockingTest {
        // Create dummy data
        val dummyData = createDummyStoryList()

        // Create a mock apiServiceHelper that returns the dummy data
        val apiServiceHelper = Mockito.mock(ApiServiceHelper::class.java)
        `when`(apiServiceHelper.getStoryList(anyInt(), anyInt())).thenReturn(dummyData)

        // Create a real instance of PagingListRepository with the mock apiServiceHelper
        val repository = PagingListRepository(apiServiceHelper)

        // Create an instance of PagingListViewModel with the real repository
        val viewModel = PagingListViewModel(repository)

        // Call the function to load story data
        val stories = viewModel.getStories()

        // Collect the paging data
        val collectedStories = mutableListOf<Story>()
        testScope.launch {
            stories.first().collectData { story ->
                collectedStories.add(story)
            }
        }

        // Assert that the data is not null
        assertNotNull(stories)

        // Assert that the amount of data is as expected
        assertEquals(dummyData.size, collectedStories.size)

        // Assert that the first data returned is correct
        assertEquals(dummyData[0], collectedStories[0])
    }

    @Test
    fun `test no story data`() = testScope.runBlockingTest {
        // Create an empty dummy data list
        val dummyData = emptyList<Story>()

        // Create a mock apiServiceHelper that returns the empty dummy data
        val apiServiceHelper = Mockito.mock(ApiServiceHelper::class.java)
        `when`(apiServiceHelper.getStoryList(anyInt(), anyInt())).thenReturn(dummyData)

        // Create a real instance of PagingListRepository with the mock apiServiceHelper
        val repository = PagingListRepository(apiServiceHelper)

        // Create an instance of PagingListViewModel with the real repository
        val viewModel = PagingListViewModel(repository)

        // Call the function to load story data
        val stories = viewModel.getStories()

        // Collect the paging data
        val collectedStories = mutableListOf<Story>()
        testScope.launch {
            stories.first().collectData { story ->
                collectedStories.add(story)
            }
        }

        // Assert that the amount of data returned is zero
        assertEquals(0, collectedStories.size)
    }


    private fun createDummyStoryList(): List<Story> {
        return listOf(
            Story("1", "Story 1", "Description 1", "https://example.com/story1.jpg", "2023-03-01", 0.0, 0.0),
            Story("2", "Story 2", "Description 2", "https://example.com/story2.jpg", "2023-03-02", 0.0, 0.0),
            Story("3", "Story 3", "Description 3", "https://example.com/story3.jpg", "2023-03-03", 0.0, 0.0)
            // Add more dummy stories if needed
        )
    }

    // Helper function to collect data from a PagingData into a List
    private suspend fun <T: Any> PagingData<T>.collectData(action: suspend (value: T) -> Unit) {
        this.pages.collect { page ->
            page.data.forEach(action)
        }
    }
}
