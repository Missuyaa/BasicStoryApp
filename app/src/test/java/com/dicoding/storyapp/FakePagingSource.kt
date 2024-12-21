package com.dicoding.storyapp

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.storyapp.data.model.Story

class FakePagingSource(
    private val data: List<Story>
) : PagingSource<Int, Story>() {

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val position = params.key ?: 1
            val start = (position - 1) * params.loadSize
            val end = (position * params.loadSize).coerceAtMost(data.size)

            LoadResult.Page(
                data = data.subList(start, end),
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (end == data.size) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
