package com.github.gameinventory.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.LibraryAdd
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.gameinventory.data.local.Game
import com.github.gameinventory.ui.components.GameCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreen(
    games: List<Game>,
    onAddGame: () -> Unit,
    onEditGame: (String) -> Unit,
    onDeleteGame: (Game) -> Unit,
    onSync: () -> Unit,
    onDownloadSteam: () -> Unit,
    modifier: Modifier = Modifier
) {
    val deepBlack = Color(0xFF121212)
    val gamerRed = Color(0xFFD32F2F)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = deepBlack,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "LISTA DE JUEGOS",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                },
                actions = {
                    IconButton(onClick = onDownloadSteam) {
                        Icon(
                            imageVector = Icons.Rounded.LibraryAdd,
                            contentDescription = "Descargar de Steam",
                            tint = gamerRed // Mantenemos el rojo gamer para la acción de Steam
                        )
                    }
                    IconButton(onClick = onSync) {
                        Icon(
                            imageVector = Icons.Rounded.Sync,
                            contentDescription = "Sincronizar MockAPI",
                            tint = Color.White // Blanco para diferenciar las acciones
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddGame,
                containerColor = gamerRed,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nuevo juego"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(deepBlack)
                .padding(horizontal = 8.dp)
        ) {
            items(games, key = { it.id }) { game ->
                GameCard(
                    game = game,
                    onEditGame = onEditGame,
                    onDeleteGame = onDeleteGame
                )
            }
        }
    }
}