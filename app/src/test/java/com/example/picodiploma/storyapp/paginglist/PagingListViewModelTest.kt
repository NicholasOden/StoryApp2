package com.example.picodiploma.storyapp.paginglist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.picodiploma.storyapp.api.response.Story
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@ExperimentalCoroutinesApi
class PagingListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: PagingListViewModel

    @Test
    fun `test loading story data successfully`() = runBlockingTest {
        // Create mock stories
        val dummyData = MockTestUtil.mockStories()

        // Create a mock repository that returns the mock stories
        val repository = mock(PagingListRepository::class.java)
        val storiesLiveData = MutableLiveData<PagingData<Story>>()
        storiesLiveData.value = dummyData
        `when`(repository.getStories()).thenReturn(storiesLiveData)

        // Create an instance of PagingListViewModel with the mock repository
        viewModel = PagingListViewModel(repository)

        // Call the function to load story data
        val stories = viewModel.getStories()

        // Observe the LiveData to collect the paging data
        val collectedStories = mutableListOf<Story>()
        stories.observeForever { pagingData ->
            pagingData?.let {
                collectedStories.addAll(it)
            }
        }

        // Assert that the data is not null
        assertNotNull(stories.value)

        // Assert that the amount of data is as expected
        assertEquals(dummyData.size, collectedStories.size)

        // Assert that the first data returned is correct
        assertEquals(dummyData[0], collectedStories[0])
    }

    @Test
    fun `test no story data`() = runBlockingTest {
        // Create an empty dummy data list
        val dummyData = emptyList<Story>()

        // Create a mock repository that returns the empty dummy data
        val repository = mock(PagingListRepository::class.java)
        val storiesLiveData = MutableLiveData<PagingData<Story>>()
        storiesLiveData.value = PagingData.from(dummyData)
        `when`(repository.getStories()).thenReturn(storiesLiveData)

        // Create an instance of PagingListViewModel with the mock repository
        viewModel = PagingListViewModel(repository)

        // Call the function to load story data
        val stories = viewModel.getStories()

        // Observe the LiveData to collect the paging data
        val collectedStories = mutableListOf<Story>()
        stories.observeForever { pagingData ->
            pagingData?.let {
                collectedStories.addAll(it)
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
}
