package com.kostas.kostasapp.core.network.model

data class CharactersResponseDto(
    val count: Int,
    val totalPages: Int,
    val nextPage: String?,
    val data: List<HeroDto>
)