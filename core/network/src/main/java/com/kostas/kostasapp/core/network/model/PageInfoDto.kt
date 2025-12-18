package com.kostas.kostasapp.core.network.model

import com.google.gson.annotations.SerializedName

data class PageInfoDto(
    @SerializedName("totalPages") val totalPages: Int? = null,
    @SerializedName("count") val count: Int? = null,
    @SerializedName("previousPage") val previousPage: String? = null,
    @SerializedName("nextPage") val nextPage: String? = null
)