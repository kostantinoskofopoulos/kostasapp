package com.kostas.kostasapp.core.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kostas.kostasapp.core.data.mapper.toDomain
import com.kostas.kostasapp.core.model.Hero
import com.kostas.kostasapp.core.network.DisneyApiService
import kotlinx.coroutines.CancellationException

internal class HeroesPagingSource(
    private val api: DisneyApiService,
    private val nameQuery: String?
) : PagingSource<Int, Hero>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Hero> =
        try {
            val page = params.key ?: FIRST_PAGE

            val response = api.getCharacters(
                page = page,
                pageSize = params.loadSize,
                name = nameQuery?.ifBlank { null }
            )

            val heroes = response.data
                .map { it.toDomain() }
                .sortedBy { it.name.orEmpty() }

            LoadResult.Page(
                data = heroes,
                prevKey = if (page == FIRST_PAGE) null else page - 1,
                nextKey = if (heroes.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            LoadResult.Error(e)
        }

    override fun getRefreshKey(state: PagingState<Int, Hero>): Int? =
        state.anchorPosition?.let { anchor ->
            val anchorPage = state.closestPageToPosition(anchor)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    private companion object {
        const val FIRST_PAGE = 1
    }
}