package com.github.gameinventory.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.github.gameinventory.data.local.Game
import com.github.gameinventory.R


@Composable
fun GameCard(
    game: Game,
    onEditGame: (String) -> Unit,
    onDeleteGame: (Game) -> Unit,
    modifier: Modifier = Modifier
) {

    val cardBackground = Color(0xFF1E1E1E)
    val gamerRed = Color(0xFFD32F2F)
    val textPrimary = Color.White
    val textSecondary = Color(0xFFB0B0B0)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 4.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data(game.backgroundImage)
                        .crossfade(true)
                        .build(),
                    contentDescription = game.name,
                    error = painterResource(id = R.drawable.ic_broken_image),
                    placeholder = painterResource(id = R.drawable.loading_img),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(85.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Column(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = game.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        if (game.pendingSync) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Pendiente",
                                tint = gamerRed,
                                modifier = Modifier.padding(start = 6.dp).size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = game.slug,
                        fontSize = 14.sp,
                        color = textSecondary
                    )
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End
                ) {
                    IconButton(onClick = { onEditGame(game.id) }) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Editar",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { onDeleteGame(game) }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Eliminar",
                            tint = gamerRed
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 0.5.dp,
                color = Color.DarkGray
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    Text("Rating: ", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = gamerRed)
                    Text("${game.rating}", fontSize = 14.sp, color = textPrimary)
                }
                Row {
                    Text("Metacritic: ", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = gamerRed)
                    Text("${game.metacriticScore ?: "N/A"}", fontSize = 14.sp, color = textPrimary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lanzamiento: ", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = textSecondary)
                Text(game.released ?: "Desconocido", fontSize = 13.sp, color = textPrimary)
            }
        }
    }
}