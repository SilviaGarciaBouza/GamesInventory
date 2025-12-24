package com.github.gameinventory.network

import RemoteGame
import com.github.gameinventory.data.remote.GameResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SteamApiService {
    /**
     * Obtiene la lista de juegos desde Steam/RAWG usando la API Key.
     */
    @GET("games")
    suspend fun searchSteamGames(
        @Query("key") apiKey: String = com.github.gameinventory.BuildConfig.STEAM_API_KEY    ): Response<GameResponse>
}