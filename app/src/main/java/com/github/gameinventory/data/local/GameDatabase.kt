package com.github.gameinventory.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Game::class],
    version = 1,
    exportSchema = false
)
abstract class GameDatabase: RoomDatabase() {
    //instancia de dao
    abstract fun gameDao(): GameDao
    //instancia bd si aun no esta creada
    companion object{
        @Volatile
        private var Instance: GameDatabase? = null
        fun getdataBase(context: Context): GameDatabase{
            return Instance ?: synchronized(this){
                Room.databaseBuilder(
                    context,
                    GameDatabase::class.java,
                    "games_database"
                ).fallbackToDestructiveMigration()
                    .build()
                    .also { Instance=it }
            }
        }

    }
}