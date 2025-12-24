package com.github.gameinventory.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao{
    //locales
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(game: Game)
    @Update
    suspend fun update(game: Game)
    //borrar tras sincromnizar
    @Query("DELETE FROM games where id= :id")
    suspend fun delete(id: String)
    //lectura reactiva: reacciona automaticamente a los cambios
    @Query("SELECT * FROM games where pendingDelete=0")
     fun getGamesStream(): Flow<List<Game>>
    //sincronizacion cliente->servidor
    @Query("SELECT * FROM games WHERE pendingSync = 1 AND pendingDelete = 0")
    suspend fun getPendingUpdates(): List<Game>
    @Query("SELECT * FROM games WHERE pendingDelete = 1 ")
    suspend fun getPendingDeletes(): List<Game>
    //sincronizacion servidor->cliente
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGames(games: List<Game>)
    @Update
    suspend fun updateGames(games: List<Game>)
    @Transaction
    suspend fun upsertSync(games: List<Game>) {
        insertGames(games)
        updateGames(games)
    }
    @Query("SELECT id from games")
    suspend fun getIds(): List<String>
    //resetear
    @Query("UPDATE games set pendingSync=0 where id in (:ids)")
    suspend fun resetPendingSync(ids: List<String>)
//Para evitar duplicar juegos de Steam al buscarlos
    @Query("SELECT EXISTS(SELECT 1 FROM games WHERE id = :id)")
    suspend fun exists(id: String): Boolean
}
