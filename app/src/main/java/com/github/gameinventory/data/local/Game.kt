package com.github.gameinventory.data.local

import RemoteGame
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName

@Entity(tableName = "games")
data class Game(
    @PrimaryKey val id: String,
    val slug: String,
    val name: String,
    val released: String? = null,
    val rating: Double = 0.0,
   val metacriticScore: Int? = null,
     val backgroundImage: String? = null,
    //sincronizacion hibrida
    val pendingSync: Boolean = false,
    val pendingDelete: Boolean = false


)
fun Game.toRemote(): RemoteGame {
    return RemoteGame(
        id = if (id.startsWith("local_")) null else id,
        slug=slug,
        name=name,
        released=released,
        rating=rating,
        metacriticScore=metacriticScore,
        backgroundImage=backgroundImage)
}