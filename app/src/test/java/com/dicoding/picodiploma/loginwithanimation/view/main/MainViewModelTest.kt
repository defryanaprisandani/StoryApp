package com.dicoding.picodiploma.loginwithanimation.view.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.picodiploma.loginwithanimation.CoroutineTestRule
import com.dicoding.picodiploma.loginwithanimation.DataDummy
import com.dicoding.picodiploma.loginwithanimation.LiveDataTestUtils.getOrAwaitValue
import com.dicoding.picodiploma.loginwithanimation.PageTestDataSource
import com.dicoding.picodiploma.loginwithanimation.adapter.StoryAdapter
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.retrorfit.remote.ListStoryItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private lateinit var mainViewModel: MainViewModel

    @Mock
    private lateinit var repository: UserRepository

    private val storyList = DataDummy.generateStoryList()

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(repository)
    }

    @Test
    fun `successfully Get Stories & Not Null`() = runTest {
        val dataSource = PageTestDataSource.snapshot(storyList)
        val listStory = MutableLiveData<PagingData<ListStoryItem>>()
        listStory.value = dataSource

        Mockito.`when`(repository.getStories()).thenReturn(listStory)

        val actualStories = mainViewModel.storyList.getOrAwaitValue()

        assertNotNull(actualStories) // Ensure the LiveData is not null

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = listUpdateCallback,
            mainDispatcher = coroutinesTestRule.testDispatcher,
            workerDispatcher = coroutinesTestRule.testDispatcher
        )

        differ.submitData(actualStories)

        Mockito.verify(repository).getStories()
        assertNotNull(differ.snapshot())
        assertEquals(storyList.size, differ.snapshot().size)
        assertEquals(storyList[0], differ.snapshot()[0])
    }

    @Test
    fun `failed to Get Stories but Not Null`() = runTest {
        val emptyStoryList: MutableList<ListStoryItem> = mutableListOf()
        val dataSource = PageTestDataSource.snapshot(emptyStoryList)

        val listStory = MutableLiveData<PagingData<ListStoryItem>>()
        listStory.value = dataSource

        Mockito.`when`(repository.getStories()).thenReturn(listStory)

        val actualStories = mainViewModel.storyList.getOrAwaitValue()

        assertNotNull(actualStories) // Ensure the LiveData is not null

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = listUpdateCallback,
            mainDispatcher = coroutinesTestRule.testDispatcher,
            workerDispatcher = coroutinesTestRule.testDispatcher
        )

        differ.submitData(actualStories)

        Mockito.verify(repository).getStories()
        assertNotNull(differ.snapshot())
        assertEquals(0, differ.snapshot().size)
    }

    private val listUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}
