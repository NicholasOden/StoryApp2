package com.example.picodiploma.storyapp.paginglist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import com.example.picodiploma.storyapp.api.response.Story
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.paging.*
import androidx.recyclerview.widget.ListUpdateCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.runner.RunWith
import kotlinx.coroutines.flow.first
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import kotlinx.coroutines.flow.first

import org.junit.Assert.*


@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class PagingListViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    @get:Rule
    val mainDispatcherRules = CoroutineTestRule()


    @Mock
    private lateinit var repository: PagingListRepository


    @Test
    fun `when Get Story Should Not Null and Return Success`() = runTest {
        val dummyStory = generateDummyStoryResponse()
        val pagingData: PagingData<Story> = StoryPagingSource.snapshot(dummyStory)
        val expectedStory = flowOf(pagingData)

        `when`(repository.getStories()).thenReturn(expectedStory)

        val pagingListViewModel = PagingListViewModel(repository)
        val actualStory: PagingData<Story> = pagingListViewModel.getStories().first()

        val differ = AsyncPagingDataDiffer(
            diffCallback = PagingCardAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)
        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0], differ.snapshot()[0])

    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<Story> = PagingData.from(emptyList())
        val expectedQuote = MutableLiveData<PagingData<Story>>()
        expectedQuote.value = data
        `when`(repository.getStories()).thenReturn(expectedQuote.asFlow())

        val pagingListViewModel = PagingListViewModel(repository)
        val actualStory: PagingData<Story> = pagingListViewModel.getStories().first()
        val differ = AsyncPagingDataDiffer(
            diffCallback = PagingCardAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStory)
        assertEquals(0, differ.snapshot().size)
    }

    fun generateDummyStoryResponse(): List<Story> {
        val storyList: MutableList<Story> = arrayListOf()
        for (i in 0..10) {
            val story = Story(
                "1",
                "Story 1",
                "Description 1",
                "https://example.com/story1.jpg",
                "2023-03-01",
                0.0,
                0.0
            )
            storyList.add(story)
        }
        return storyList
    }
}

class StoryPagingSource : PagingSource<Int, LiveData<List<Story>>>() {
    companion object {
        fun snapshot(items: List<Story>): PagingData<Story> {
            return PagingData.from(items)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<Story>>>): Int {
        return 0
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload:Any?){}
}