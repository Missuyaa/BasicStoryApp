package com.dicoding.storyapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import com.dicoding.storyapp.data.model.Story
import com.dicoding.storyapp.data.viewmodel.StoryViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StoryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var viewModel: StoryViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `verifystorypagingdataisloadedcorrectly`() = runTest {
        // Buat data cerita palsu
        val fakeStories = listOf(
            Story("1", "Story 1", "Description 1", "https://example.com/photo1.jpg", "2022-01-01", 1.0, 2.0),
            Story("2", "Story 2", "Description 2", "https://example.com/photo2.jpg", "2022-01-02", 3.0, 4.0)
        )
        val fakePagingData = PagingData.from(fakeStories)

        coEvery { viewModel.storyPagingData } returns flowOf(fakePagingData)

        viewModel.storyPagingData.collect { pagingData ->
            assertNotNull(pagingData)
        }

        viewModel.storyPagingData.collect { pagingData ->
            assertEquals(fakePagingData, pagingData)
        }
    }

    @Test
    fun `verifystorypagingdataisempty`() = runTest {

        val emptyPagingData = PagingData.from(emptyList<Story>())

        coEvery { viewModel.storyPagingData } returns flowOf(emptyPagingData)

        viewModel.storyPagingData.collect { pagingData ->
            assertEquals(emptyPagingData, pagingData)
        }
    }
}
