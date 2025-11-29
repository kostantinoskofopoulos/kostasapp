package com.kostas.kostasapp.core.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kostas.kostasapp.core.model.Hero
import com.kostas.kostasapp.core.network.DisneyApiService

class HeroesPagingSource(
    private val api: DisneyApiService,
    private val query: String?
) : PagingSource<Int, Hero>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Hero> =
        try {
            val page = params.key ?: 1
            val response = api.getCharacters(
                page = page,
                pageSize = params.loadSize,
                name = query
            )

            // Αλφαβητική σειρά μέσα στο page
            val heroes = response.data.sortedBy { it.name.orEmpty() }

            LoadResult.Page(
                data = heroes,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (heroes.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    override fun getRefreshKey(state: PagingState<Int, Hero>): Int? =
        state.anchorPosition?.let { anchor ->
            val page = state.closestPageToPosition(anchor)
            page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
        }
}