package com.kostas.kostasapp.core.network.model

import com.google.gson.annotations.SerializedName

data class HeroDto(
    @SerializedName("_id")
    val id: Int,
    val name: String?,
    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("url")
    val sourceUrl: String?,
    val films: List<String> = emptyList(),
    val tvShows: List<String> = emptyList(),
    val videoGames: List<String> = emptyList(),
    val allies: List<String> = emptyList(),
    val enemies: List<String> = emptyList()
)