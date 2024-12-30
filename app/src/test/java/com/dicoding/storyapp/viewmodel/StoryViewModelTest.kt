package com.dicoding.storyapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dicoding.storyapp.DataDummy
import com.dicoding.storyapp.data.viewmodel.StoryViewModel
import com.dicoding.storyapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `when fetching stories is successful, stories should not be null and data is correct`() = runTest {
        // Data dummy
        val dummyStories = DataDummy.generateDummyStories()

        // ViewModel instance
        val storyViewModel = StoryViewModel()
        storyViewModel.setStoriesForTesting(dummyStories)

        // Test LiveData
        val result = storyViewModel.storiesLiveData.getOrAwaitValue()

        assertNotNull(result)
        assertEquals(dummyStories.size, result.size)
        assertEquals(dummyStories[0], result[0])
    }

    @Test
    fun `when no stories are returned, stories should be empty`() = runTest {
        val storyViewModel = StoryViewModel()
        storyViewModel.setStoriesForTesting(emptyList())

        val result = storyViewModel.storiesLiveData.getOrAwaitValue()

        assertNotNull(result)
        assertEquals(0, result.size)
    }
}
