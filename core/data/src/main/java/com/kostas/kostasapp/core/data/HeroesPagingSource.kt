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

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Hero> = try {
        val page = params.key ?: FIRST_PAGE
        val pageSize = params.loadSize.coerceIn(MIN_PAGE_SIZE, MAX_PAGE_SIZE)

        val response = api.getCharacters(
            page = page,
            pageSize = pageSize,
            name = nameQuery?.trim()?.takeIf { it.isNotBlank() }
        )

        val heroes = response.data.map { it.toDomain() }
        val totalPages = response.info?.totalPages ?: 1

        val endReached = heroes.isEmpty() || page >= totalPages
        val nextKey = if (endReached) null else page + 1
        val prevKey = if (page == FIRST_PAGE) null else page - 1

        LoadResult.Page(
            data = heroes,
            prevKey = prevKey,
            nextKey = nextKey
        )
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        LoadResult.Error(e)
    }

    override fun getRefreshKey(state: PagingState<Int, Hero>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.let { anchorPage ->
                anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1)
            }
        }

    private companion object {
        const val FIRST_PAGE = 1
        const val MIN_PAGE_SIZE = 20
        const val MAX_PAGE_SIZE = 60
    }
}