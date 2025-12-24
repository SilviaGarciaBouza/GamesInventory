package com.github.gameinventory.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.gameinventory.data.local.Game


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameFormScreen(
    games: List<Game>,
    gameId: String?,
    onDone: (Game) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gamerRed = Color(0xFFD32F2F)
    val deepBlack = Color(0xFF121212)

    Scaffold(
        containerColor = deepBlack,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (gameId == null) "CREAR JUEGO" else "MODIFICAR JUEGO",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = gamerRed
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                )
            )
        }
    ) { contentPadding ->
        GameEditScreen(games, gameId, onDone, Modifier.padding(contentPadding))
    }
}


@Composable
fun GameEditScreen(
    games: List<Game>,
    gameId: String?,
    onDone: (Game) -> Unit,
    modifier: Modifier = Modifier
) {
    val existingGame = games.find { it.id == gameId }
    val gamerRed = Color(0xFFD32F2F)
    val deepBlack = Color(0xFF121212)

    var name by remember { mutableStateOf(existingGame?.name ?: "") }
    var slug by remember { mutableStateOf(existingGame?.slug ?: "") }
    var released by remember { mutableStateOf(existingGame?.released ?: "") }
    var rating by remember { mutableStateOf(existingGame?.rating?.toString() ?: "") }
    var metacriticScore by remember { mutableStateOf(existingGame?.metacriticScore?.toString() ?: "") }
    var backgroundImage by remember { mutableStateOf(existingGame?.backgroundImage ?: "") }

    val scrollState = rememberScrollState()

    val customTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = gamerRed,
        unfocusedBorderColor = Color.Gray,
        focusedLabelColor = gamerRed,
        unfocusedLabelColor = Color.LightGray,
        cursorColor = gamerRed,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(deepBlack)
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre del Juego") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors
        )

        OutlinedTextField(
            value = slug,
            onValueChange = { slug = it },
            label = { Text("Slug (Identificador URL)") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors
        )

        OutlinedTextField(
            value = released,
            onValueChange = { released = it },
            label = { Text("Lanzamiento (AAAA-MM-DD)") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors
        )

        OutlinedTextField(
            value = rating,
            onValueChange = { rating = it },
            label = { Text("Rating (0.0 - 5.0)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = customTextFieldColors
        )

        OutlinedTextField(
            value = metacriticScore,
            onValueChange = { metacriticScore = it },
            label = { Text("Puntuación Metacritic") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = customTextFieldColors
        )

        OutlinedTextField(
            value = backgroundImage,
            onValueChange = { backgroundImage = it },
            label = { Text("URL de la Imagen") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val gameToSave = Game(
                    id = gameId ?: "local_${System.nanoTime()}",
                    name = name,
                    slug = slug,
                    released = released,
                    rating = rating.toDoubleOrNull() ?: 0.0,
                    metacriticScore = metacriticScore.toIntOrNull(),
                    backgroundImage = backgroundImage,
                    pendingSync = true
                )
                onDone(gameToSave)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = name.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = gamerRed,
                contentColor = Color.White,
                disabledContainerColor = Color.DarkGray
            )
        ) {
            Text(
                text = if (gameId == null) "CONFIRMAR CREACIÓN" else "GUARDAR CAMBIOS",
                fontWeight = FontWeight.Bold
            )
        }
    }
}