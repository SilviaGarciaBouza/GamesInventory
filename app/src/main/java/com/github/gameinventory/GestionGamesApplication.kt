package com.github.gameinventory

import android.app.Application
import com.github.gameinventory.data.AppContainer
import com.github.gameinventory.data.AppdataContainer

class GestionGamesApplication: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container= AppdataContainer(this)
    }
}