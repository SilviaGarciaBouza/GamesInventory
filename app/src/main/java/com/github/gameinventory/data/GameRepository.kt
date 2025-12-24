package com.github.gameinventory.data

import RemoteGame
import com.github.gameinventory.data.local.Game
import com.github.gameinventory.data.local.GameDao
import com.github.gameinventory.data.local.toRemote
import com.github.gameinventory.data.remote.*
import com.github.gameinventory.network.MockApiService
import com.github.gameinventory.network.SteamApiService
import kotlinx.coroutines.flow.Flow
import toLocal

sealed class RepositoryResult {
    class Success(val message: String) : RepositoryResult()
    data class Error(val message: String, val exception: Throwable? = null) : RepositoryResult()
}

interface GameRepository {
    fun getAllGamerStream(): Flow<List<Game>>
    suspend fun insertGame(game: Game): RepositoryResult
    suspend fun updateGame(game: Game): RepositoryResult
    suspend fun deleteGame(game: Game): RepositoryResult
    suspend fun uploadPendingChanges(): RepositoryResult
    suspend fun syncFromServer(): RepositoryResult
    suspend fun fetchGamesFromSteam(): RepositoryResult
}

class DefaultGameRepository(
    private val local: GameDao,
    private val remote: MockApiService,
    private val steam: SteamApiService
) : GameRepository {

    override fun getAllGamerStream(): Flow<List<Game>> = local.getGamesStream()


    override suspend fun fetchGamesFromSteam(): RepositoryResult {
        return try {
            val response = steam.searchSteamGames()
            if (response.isSuccessful) {
                val body = response.body()
                val remoteGames = if (body != null) body.results else emptyList()
                val localGames = remoteGames.map { it.toLocal(isFromSteam = true) }

                local.upsertSync(localGames)
                RepositoryResult.Success("Importados ${localGames.size} juegos de Steam")
            } else {
                RepositoryResult.Error("Error en Steam: ${response.code()}")
            }
        } catch (e: Exception) {
            RepositoryResult.Error("Fallo de red con Steam", e)
        }
    }
    override suspend fun insertGame(game: Game): RepositoryResult {
        local.insert(game.copy(pendingSync = true))
        return try {
            val response = remote.createNewGame(game.toRemote())
            if (response.isSuccessful) {
                local.resetPendingSync(listOf(game.id))
                RepositoryResult.Success("Sincronizado con MockAPI")
            } else {
                RepositoryResult.Success("Guardado en local")
            }
        } catch (e: Exception) {
            RepositoryResult.Success("Modo Offline: Guardado local")
        }
    }

    override suspend fun updateGame(game: Game): RepositoryResult {
        local.update(game.copy(pendingSync = true))
        return try {
            if (remote.updateGAme(game.toRemote(), game.id).isSuccessful) {
                local.resetPendingSync(listOf(game.id))
                RepositoryResult.Success("Actualización sincronizada")
            } else {
                RepositoryResult.Success("Actualizado en local")
            }
        } catch (e: Exception) {
            RepositoryResult.Success("Actualizado localmente (Offline)")
        }
    }

    override suspend fun deleteGame(game: Game): RepositoryResult {
        if (game.id.startsWith("local_")) {
            local.delete(game.id)
            return RepositoryResult.Success("Eliminado")
        }
        local.update(game.copy(pendingDelete = true, pendingSync = true))
        return try {
            if (remote.deleteGame(game.id).isSuccessful) {
                local.delete(game.id)
                RepositoryResult.Success("Borrado permanente")
            } else {
                RepositoryResult.Success("Borrado local pendiente")
            }
        } catch (e: Exception) {
            RepositoryResult.Success("Borrado Offline")
        }
    }


    override suspend fun uploadPendingChanges(): RepositoryResult {
        return try {
            val pendingUpdates = local.getPendingUpdates()
            val pendingDeletes = local.getPendingDeletes()

            for (game in pendingUpdates) {
                val resUpdate = remote.updateGAme(game.toRemote(), game.id)

                if (resUpdate.isSuccessful) {
                    local.resetPendingSync(listOf(game.id))
                } else {
                    val resCreate = remote.createNewGame(game.toRemote())

                    if (resCreate.isSuccessful) {
                        if (game.id.startsWith("local_")) {
                            local.delete(game.id)
                            val newGameFromServer = resCreate.body()
                            if (newGameFromServer != null) {
                                local.insert(newGameFromServer.toLocal())
                            }
                        } else {
                            local.resetPendingSync(listOf(game.id))
                        }
                    }
                }
            }

            for (game in pendingDeletes) {
                if (remote.deleteGame(game.id).isSuccessful) {
                    local.delete(game.id)
                }
            }

            RepositoryResult.Success("¡Sincronización con MockAPI completada!")
        } catch (e: Exception) {
            RepositoryResult.Error("Fallo en la conexión: ${e.message}", e)
        }
    }


    override suspend fun syncFromServer(): RepositoryResult {
        return try {
            val response = remote.getAllGames()
            if (response.isSuccessful) {
                val body = response.body()
                val remoteGames: List<RemoteGame> = if (body != null) body else emptyList()

                local.upsertSync(remoteGames.map { it.toLocal() })
                RepositoryResult.Success("Descarga completa")
            } else {
                RepositoryResult.Error("Error servidor: ${response.code()}")
            }
        } catch (e: Exception) {
            RepositoryResult.Error("Fallo red", e)
        }
    }
}