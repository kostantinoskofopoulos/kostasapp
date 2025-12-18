package com.kostas.kostasapp.core.network.model

import com.google.gson.annotations.SerializedName

data class HeroDetailResponseDto(
    @SerializedName("info")
    val info: PageInfoDto? = null,

    @SerializedName("data")
    val data: HeroDto
)