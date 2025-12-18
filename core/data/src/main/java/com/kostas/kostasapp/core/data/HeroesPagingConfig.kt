package com.kostas.kostasapp.core.data

import androidx.paging.PagingConfig

object HeroesPagingConfig {

    private const val PAGE_SIZE = 20

    val PAGING_CONFIG: PagingConfig = PagingConfig(
        pageSize = PAGE_SIZE,
        initialLoadSize = PAGE_SIZE * 2, // good UX without huge burst
        prefetchDistance = PAGE_SIZE,     // keeps scrolling smooth
        enablePlaceholders = false
    )
}