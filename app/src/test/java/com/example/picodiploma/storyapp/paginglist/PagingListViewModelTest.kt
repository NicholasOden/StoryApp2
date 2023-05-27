package com.example.picodiploma.storyapp.paginglist


import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class PagingListViewModelTest {

    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    @Mock
    private lateinit var repository: PagingListRepository

    private lateinit var viewModel: PagingListViewModel

    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = PagingListViewModel(repository)
    }

    @Test
    fun `getStories returns non-empty list`() = testScope.runBlockingTest {
        val mockPagingData = MockTestUtil.mockStories()
        `when`(repository.getStories()).thenReturn(flowOf(mockPagingData))

        val result = viewModel.getStories()
        val list = result.toList()

        assertNotNull(list)
        assertEquals(3, list.size)
        assertEquals(mockPagingData, list[0])
    }

    @Test
    fun `getStories returns empty list`() = testScope.runBlockingTest {
        val mockPagingData = MockTestUtil.mockEmptyStories()
        `when`(repository.getStories()).thenReturn(flowOf(mockPagingData))

        val result = viewModel.getStories()
        val list = result.toList()

        assertEquals(0, list.size)
    }

    @After
    fun cleanup() {
        testScope.cleanupTestCoroutines()
    }

}
