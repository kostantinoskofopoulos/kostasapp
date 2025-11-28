package com.kostas.kostasapp.core.network.model

import com.kostas.kostasapp.core.model.Hero

data class CharactersResponse(
    val count: Int,
    val totalPages: Int,
    val nextPage: String?,
    val data: List<Hero>
)