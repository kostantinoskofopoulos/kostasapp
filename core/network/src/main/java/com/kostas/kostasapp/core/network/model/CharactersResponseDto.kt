package com.kostas.kostasapp.core.network.model

import com.google.gson.annotations.SerializedName

/**
 * DTO representing the paginated response of the characters endpoint
 * from the Disney API.
 *
 * This model is used only in the network layer and is mapped to
 * domain models (e.g. [Hero]) via dedicated mappers.
 */
data class CharactersResponseDto(

    /** Total number of items matching the query across all pages. */
    @SerializedName("count")
    val count: Int,

    /** Total number of available pages. */
    @SerializedName("totalPages")
    val totalPages: Int,

    /** URL or token of the next page, if available. */
    @SerializedName("nextPage")
    val nextPage: String?,

    /** List of character entries in the current page. */
    @SerializedName("data")
    val data: List<HeroDto>
)