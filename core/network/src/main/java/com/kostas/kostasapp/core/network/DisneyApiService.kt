package com.kostas.kostasapp.core.network

import com.kostas.kostasapp.core.network.model.CharactersResponseDto
import com.kostas.kostasapp.core.network.model.HeroDetailResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DisneyApiService {

    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("name") name: String? = null
    ): CharactersResponseDto

    @GET("character/{id}")
    suspend fun getCharacterById(
        @Path("id") id: Int
    ): HeroDetailResponseDto
}