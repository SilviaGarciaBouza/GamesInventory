package com.github.gameinventory.data.remote

import RemoteGame
import kotlinx.serialization.Serializable

@Serializable
data class GameResponse(
    val results: List<RemoteGame>
)
