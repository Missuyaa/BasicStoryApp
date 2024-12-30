package com.dicoding.storyapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.testing.AsyncPagingDataDiffer
import androidx.paging.testing.NoopListCallback
import androidx.recyclerview.widget.DiffUtil
import com.dicoding.storyapp.data.model.Story
import com.dicoding.storyapp.data.viewmodel.StoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.manipulation.Ordering
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class StoryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: StoryViewModel

    @Before
    fun setUp() {
        // Simulasi DataStoreManager dan Context
        val fakeDataStoreManager = FakeDataStoreManager()
        val mockContext = mock(Ordering.Context::class.java)

        // Buat instance StoryViewModel dengan dependensi palsu
        viewModel = StoryViewModel(fakeDataStoreManager, mockContext)
    }

    @Test
    fun `verify storyPagingData returns correct PagingData`() = runTest {
        // Buat data cerita palsu
        val fakeStories = listOf(
            Story("1", "Story 1", "Description 1", "https://example.com/photo1.jpg", "2022-01-01", 1.0, 2.0),
            Story("2", "Story 2", "Description 2", "https://example.com/photo2.jpg", "2022-01-02", 3.0, 4.0)
        )

        // Dapatkan PagingData dari StoryViewModel
        val pagingData = viewModel.storyPagingData.first()

        // Validasi data tidak null
        assertNotNull(pagingData)

        // Gunakan AsyncPagingDataDiffer untuk memvalidasi PagingData
        val differ = AsyncPagingDataDiffer(
            diffCallback = object : DiffUtil.ItemCallback<Story>() {
                override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean =
                    oldItem.id == newItem.id

                override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean =
                    oldItem == newItem
            },
            updateCallback = NoopListCallback(),
            mainDispatcher = Dispatchers.Main,
            workerDispatcher = Dispatchers.IO
        )
        differ.submitData(pagingData)

        // Tunggu data selesai dimuat
        advanceUntilIdle()

        // Validasi jumlah data
        assertEquals(fakeStories.size, differ.itemCount)

        // Validasi data pertama
        val firstStory = differ.snapshot()[0]
        assertNotNull(firstStory)
        assertEquals("Story 1", firstStory?.name)
        assertEquals("Description 1", firstStory?.description)
    }

    @Test
    fun `verify storyPagingData returns empty when no data`() = runTest {
        // Kosongkan data cerita
        val emptyStories = emptyList<Story>()

        // Dapatkan PagingData dari StoryViewModel
        val pagingData = viewModel.storyPagingData.first()

        // Gunakan AsyncPagingDataDiffer untuk memvalidasi PagingData
        val differ = AsyncPagingDataDiffer(
            diffCallback = object : DiffUtil.ItemCallback<Story>() {
                override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean =
                    oldItem.id == newItem.id

                override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean =
                    oldItem == newItem
            },
            updateCallback = NoopListCallback(),
            mainDispatcher = Dispatchers.Main,
            workerDispatcher = Dispatchers.IO
        )
        differ.submitData(pagingData)

        // Tunggu data selesai dimuat
        advanceUntilIdle()

        // Validasi jumlah data nol
        assertEquals(0, differ.itemCount)
    }
}
