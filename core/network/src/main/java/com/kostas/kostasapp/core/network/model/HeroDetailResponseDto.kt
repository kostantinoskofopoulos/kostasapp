package com.kostas.kostasapp.core.network.model

import com.google.gson.annotations.SerializedName

/**
 * DTO representing the response of the hero details endpoint.
 *
 * The Disney API wraps a single hero payload inside a top-level `data` field.
 * This model is used only in the network layer and is mapped to the domain
 * [com.kostas.kostasapp.core.model.Hero] via mappers.
 */
data class HeroDetailResponseDto(

    /** The hero payload returned by the API. */
    @SerializedName("data")
    val data: HeroDto
)