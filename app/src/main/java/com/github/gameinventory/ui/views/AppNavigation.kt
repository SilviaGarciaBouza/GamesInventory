package com.github.gameinventory.ui.views

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.gameinventory.viewmodel.GameViewModel

/**
 * Controlador principal de navegación para la sección de juegos (sin sensor).
 *
 * Gestiona:
 * - El NavController para movernos entre pantallas.
 * - El acceso al GameViewModel y su estado (lista de juegos).
 * - Los mensajes emergentes (Snackbars) mediante eventos.
 * - Las rutas de la lista y del formulario.
 */
@Composable
fun AppNavigation(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    val gameViewModel: GameViewModel = viewModel(factory = GameViewModel.Factory)
    val games by gameViewModel.games.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarHostState) {
        gameViewModel.events.collect { message ->
            snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        val paddingModifier = Modifier.padding(paddingValues)

        NavHost(
            navController = navController,
            startDestination = "game_list",
            modifier = paddingModifier
        ) {

            // ---------------------------
            // Pantalla: Lista de xogos
            // ---------------------------
            composable(route = "game_list") {
                GameListScreen(
                    games = games,
                    onAddGame = { navController.navigate("game_form") },
                    onEditGame = { id -> navController.navigate("game_form/$id") },
                    onDeleteGame = { game -> gameViewModel.deleteGame(game) },
                    onSync = { gameViewModel.sync() },
                    onDownloadSteam = { gameViewModel.downloadFromSteam() }
                )
            }

            // ----------------------------------------
            // Pantalla: Formulario para crear xogo
            // ----------------------------------------
            composable(route = "game_form") {
                GameFormScreen(
                    games = games,
                    gameId = null,
                    onDone = { game ->
                        gameViewModel.insertGame(game)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // ----------------------------------------
            // Pantalla: Formulario para editar xogo
            // ----------------------------------------
            composable(
                route = "game_form/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                GameFormScreen(
                    games = games,
                    gameId = id,
                    onDone = { game ->
                        gameViewModel.updateGame(game)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}