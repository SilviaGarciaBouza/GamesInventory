package com.github.gameinventory.data

import android.content.Context
import com.github.gameinventory.data.local.GameDatabase
import com.github.gameinventory.network.MockApiService
import com.github.gameinventory.network.SteamApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val repository: GameRepository
}

class AppdataContainer(private val context: Context) : AppContainer {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val retrofitMock = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl("https://694811221ee66d04a44ea00f.mockapi.io/")
        .build()

    private val retrofitSteam = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl("https://api.rawg.io/api/")
        .build()

    private val mockService: MockApiService by lazy {
        retrofitMock.create(MockApiService::class.java)
    }

    private val steamService: SteamApiService by lazy {
        retrofitSteam.create(SteamApiService::class.java)
    }

    private val database: GameDatabase by lazy {
        GameDatabase.getdataBase(context)
    }

    override val repository: GameRepository by lazy {
        DefaultGameRepository(database.gameDao(), mockService, steamService)
    }
}