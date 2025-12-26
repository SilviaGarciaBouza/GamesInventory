package com.github.gameinventory.sensor

import android.content.Context
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.gameinventory.viewmodel.GameViewModel

class SensorCoordinator(context: Context,
    private val viewModelGame: GameViewModel) {
    private val sensor= SensorDetector(context){ actionCo()}
    private fun actionCo(){
        viewModelGame.sync()
    }
    public fun start(){
        sensor.start()
    }
    public fun close(){
        sensor.close()
    }

}