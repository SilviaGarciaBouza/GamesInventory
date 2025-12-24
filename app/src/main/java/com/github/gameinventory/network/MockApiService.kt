package com.github.gameinventory.network

import RemoteGame
import com.github.gameinventory.data.remote.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

//games?key=6f75710a3b094c24a3ca75979e0eb313
interface MockApiService {
 @GET("games")
 suspend fun getAllGames(): Response<List<RemoteGame>>
 @POST("games")
 suspend fun createNewGame(@Body game: RemoteGame): Response<RemoteGame>
 @PUT("games/{id}")
 suspend fun updateGAme(@Body game: RemoteGame, @Path("id") id: String): Response<RemoteGame>
 @DELETE("games/{id}")
 suspend fun deleteGame(@Path("id") id: String):Response<Unit>

}