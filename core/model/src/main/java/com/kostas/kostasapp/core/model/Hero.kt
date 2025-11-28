package com.kostas.kostasapp.core.model

import com.google.gson.annotations.SerializedName

data class Hero(
    @SerializedName("_id")
    val id: Int,
    val name: String?,
    val imageUrl: String?,
    val sourceUrl: String?,
    val films: List<String> = emptyList(),
    val tvShows: List<String> = emptyList(),
    val videoGames: List<String> = emptyList(),
    val allies: List<String> = emptyList(),
    val enemies: List<String> = emptyList()
)