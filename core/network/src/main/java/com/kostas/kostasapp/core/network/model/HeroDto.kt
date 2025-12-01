package com.kostas.kostasapp.core.network.model

import com.google.gson.annotations.SerializedName

/**
 * DTO representing a single hero entry returned by the Disney API.
 *
 * This model mirrors the JSON structure of the `/characters` endpoint
 * and is mapped to the domain [com.kostas.kostasapp.core.model.Hero]
 * via a dedicated mapper in the data layer.
 */
data class HeroDto(

    /** Unique identifier of the hero in the API. */
    @SerializedName("_id")
    val id: Int,

    /** Display name of the hero. */
    val name: String?,

    /** URL of the hero's primary image. */
    @SerializedName("imageUrl")
    val imageUrl: String?,

    /** Source URL pointing to more information about the hero. */
    @SerializedName("url")
    val sourceUrl: String?,

    /** List of films in which the hero appears. */
    val films: List<String> = emptyList(),

    /** List of TV shows in which the hero appears. */
    val tvShows: List<String> = emptyList(),

    /** List of video games in which the hero appears. */
    val videoGames: List<String> = emptyList(),

    /** List of allies associated with the hero. */
    val allies: List<String> = emptyList(),

    /** List of enemies associated with the hero. */
    val enemies: List<String> = emptyList()
)