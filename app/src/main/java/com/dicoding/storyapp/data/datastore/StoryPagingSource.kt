package com.dicoding.storyapp.data.datastore

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.storyapp.data.api.ApiService
import com.dicoding.storyapp.data.model.Story
import kotlinx.coroutines.delay

class StoryPagingSource(
    private val apiService: ApiService,
    private val token: String
) : PagingSource<Int, Story>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        delay(1000)
        val position = params.key ?: 1

        println("Paging Source -> Posisi: $position, LoadSize: ${params.loadSize}")

        return try {
            val response = apiService.getStories("Bearer $token", position, params.loadSize)
            if (response.isSuccessful) {
                val storyResponse = response.body()
                val nextKey = if (storyResponse?.listStory.isNullOrEmpty()) null else position + 1

                println("Paging Source -> Next Key: $nextKey, Data Count: ${storyResponse?.listStory?.size}")
                LoadResult.Page(
                    data = storyResponse?.listStory ?: emptyList(),
                    prevKey = if (position == 1) null else position - 1,
                    nextKey = nextKey
                )
            } else {
                println("Paging Source -> Error: ${response.message()}")
                LoadResult.Error(Exception(response.message()))
            }
        } catch (e: Exception) {
            println("Paging Source -> Exception: ${e.message}")
            LoadResult.Error(e)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}
