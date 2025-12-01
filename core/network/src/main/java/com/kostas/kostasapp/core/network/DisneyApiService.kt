package com.kostas.kostasapp.core.network

import com.kostas.kostasapp.core.network.model.CharactersResponseDto
import com.kostas.kostasapp.core.network.model.HeroDetailResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service definition for the Disney API.
 */
interface DisneyApiService {

    /**
     * Fetches a paginated list of characters.
     */
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("name") name: String? = null
    ): CharactersResponseDto

    /**
     * Fetches details for a single character by [id].
     */
    @GET("character/{id}")
    suspend fun getCharacterById(
        @Path("id") id: Int
    ): HeroDetailResponseDto
}