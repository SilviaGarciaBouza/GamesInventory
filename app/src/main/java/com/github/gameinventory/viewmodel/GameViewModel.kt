package com.github.gameinventory.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.gameinventory.GestionGamesApplication
import com.github.gameinventory.data.GameRepository
import com.github.gameinventory.data.RepositoryResult
import com.github.gameinventory.data.local.Game
import com.github.gameinventory.sensor.SensorCoordinator
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GameViewModel(private val gameRepository: GameRepository) : ViewModel() {

    // Canal para enviar eventos de un solo uso (mensajes Toast/Snackbar) a la UI
    private val _events = Channel<String>()
    val events = _events.receiveAsFlow()
    // SENSOR1/3
    private var sensorCoordinator: SensorCoordinator? = null
    /**
    SENSOR2/3
     */
    fun setupSensor(context: Context) {
        if (sensorCoordinator == null) {
            sensorCoordinator = SensorCoordinator(context, this)
            sensorCoordinator?.start()
        }
    }

    /**
     SENSOR3/3
     */
    override fun onCleared() {
        super.onCleared()
        sensorCoordinator?.close()
        sensorCoordinator = null
    }

    // Estado reactivo de la lista de juegos, se actualiza solo cuando hay cambios en Room
    val games: StateFlow<List<Game>> = gameRepository.getAllGamerStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Descarga el catálogo externo de Steam/RAWG.
     */
    fun downloadFromSteam() = viewModelScope.launch {
        _events.send("Conectando con Steam...")
        val result = gameRepository.fetchGamesFromSteam()
        when (result) {
            is RepositoryResult.Success -> _events.send(result.message)
            is RepositoryResult.Error -> _events.send("Error Steam: ${result.message}")
        }
    }

    /**
     * Sincronización completa con tu MockAPI personal.
     */
    fun sync() = viewModelScope.launch {
        _events.send("Iniciando sincronización con el servidor...")
        // Primero subimos lo que tenemos pendiente
        gameRepository.uploadPendingChanges()
        // Luego descargamos lo nuevo que haya en la nube
        val result = gameRepository.syncFromServer()

        if (result is RepositoryResult.Success) {
            _events.send("Sincronización finalizada con éxito")
        } else {
            _events.send("Sincronización completada con avisos")
        }
    }

    // --- OPERACIONES CRUD ---

    fun insertGame(game: Game) = viewModelScope.launch {
        val result = gameRepository.insertGame(game)
        if (result is RepositoryResult.Success) _events.send(result.message)
    }

    fun updateGame(game: Game) = viewModelScope.launch {
        val result = gameRepository.updateGame(game)
        if (result is RepositoryResult.Success) _events.send(result.message)
    }

    fun deleteGame(game: Game) = viewModelScope.launch {
        val result = gameRepository.deleteGame(game)
        if (result is RepositoryResult.Success) _events.send(result.message)
    }

    /**
     * Factory para instanciar el ViewModel inyectando el repositorio desde el contenedor
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GestionGamesApplication)
                val gamesRepository = application.container.repository
                GameViewModel(gameRepository = gamesRepository)
            }
        }
    }
}