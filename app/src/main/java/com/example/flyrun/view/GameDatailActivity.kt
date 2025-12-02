@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.flyrun.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import com.example.flyrun.R
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.Image
import com.example.flyrun.controller.Navigator
import com.example.flyrun.model.Game
import com.example.flyrun.model.GameId
import com.example.flyrun.model.PurchasableItem
import com.example.flyrun.ui.theme.DarkPreview
import com.example.flyrun.ui.theme.FlyRunTheme

class GameDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION") val game =
            intent.getSerializableExtra(Navigator.EXTRA_GAME) as Game
        setContent {
            FlyRunTheme(dark = true) {
                GameDetailScreen(game = game!!, onBack = { finish() })
            }
        }
    }
}

@Composable
fun GameDetailScreen(game: Game, onBack: () -> Unit) {
    val configuration = LocalConfiguration.current
    val imageHeight = (configuration.screenHeightDp * 0.3f).dp
    val selectedItem = remember { mutableStateOf<PurchasableItem?>(null) }
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(game.title, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(32.dp)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_back),
                            contentDescription = "Voltar",
                            tint = Color.White,
                            modifier = Modifier.clickable { onBack() })
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(32.dp)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_heart),
                            contentDescription = "Favorito",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            GameHeaderBanner(game = game, height = imageHeight)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = game.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            if (game.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = game.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Itens Compráveis",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(game.items) { item ->
                    ItemRow(item = item, onClick = { selectedItem.value = item })
                }
            }
        }
        if (selectedItem.value != null) {
            PurchaseSheet(item = selectedItem.value!!, onBuy = {
                selectedItem.value = null
                Toast.makeText(
                    context,
                    "Acabou de comprar o item ${it.name} por ${it.price}",
                    Toast.LENGTH_SHORT
                ).show()
            }, onCancel = { selectedItem.value = null })
        }
    }
}

@Composable
fun ItemRow(item: PurchasableItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable { onClick() }, verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(Color.White.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            if (item.imageUrl != null) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier.size(56.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Icon(
                    painterResource(item.iconRes),
                    contentDescription = item.name,
                    tint = Color.Unspecified
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(text = item.name, style = MaterialTheme.typography.labelLarge)
            if (item.description.isNotBlank()) {
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2
                )
            }
        }
        Text(
            text = item.price,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PurchaseSheet(item: PurchasableItem, onBuy: (PurchasableItem) -> Unit, onCancel: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onCancel, dragHandle = null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Confirm Purchase",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (item.imageUrl != null) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.name,
                        modifier = Modifier.size(72.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Icon(
                        painterResource(item.iconRes),
                        contentDescription = item.name,
                        tint = Color.Unspecified
                    )
                }
            }
            Text(text = item.name, style = MaterialTheme.typography.labelLarge)
            Text(
                text = item.price,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Button(
                onClick = { onBuy(item) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71))
            ) { Text("Buy Now", color = Color.White) }
            OutlinedButton(
                onClick = onCancel, modifier = Modifier.fillMaxWidth()
            ) { Text("Cancel") }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun GameHeaderBanner(game: Game, height: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height), contentAlignment = Alignment.Center
    ) {
        when {
            game.coverRes != null -> Image(
                painter = painterResource(game.coverRes),
                contentDescription = game.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            game.coverUrl != null -> AsyncImage(
                model = game.coverUrl,
                contentDescription = game.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            else -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (game.id == GameId.F1) Color(0xFFC80000) else Color(0xFF0055A4))
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.25f))
        )
        Text(text = game.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
    }
}


@DarkPreview
@Composable
fun PreviewGameDetailScreen() {
    FlyRunTheme(dark = isSystemInDarkTheme()) {
        val g = Game(
            id = GameId.MotoGP,
            title = "MotoGP™",
            description = "",
            items = listOf(
                PurchasableItem(
                    "Pacote de Equipamento do Piloto",
                    "$11.99",
                    "Fato com marcação e patrocínios oficiais.",
                    R.drawable.pacotedeequipamento,
                    imageUrl = null
                ),
                PurchasableItem(
                    "Coleção de Motos Clássicas",
                    "$8.99",
                    "Motos vintage de temporadas memoráveis.",
                    R.drawable.colesaodemotos,
                    imageUrl = null
                ),
                PurchasableItem(
                    "Expansão de Circuitos do GP",
                    "$15.99",
                    "Circuitos emblemáticos de Grandes Prémios.",
                    R.drawable.expansaodo,
                    imageUrl = null
                )
            ),
            coverUrl = null,
            coverRes = R.drawable.motogp0
        )
        GameDetailScreen(game = g, onBack = {})
    }
}

 

@DarkPreview
@Composable
fun PreviewGameDetailScreenF1() {
    FlyRunTheme(dark = isSystemInDarkTheme()) {
        val g = Game(
            id = GameId.F1,
            title = "Fórmula 1®",
            description = "A official Formula 1 racing experience with all teams, drivers and circuits.",
            items = listOf(
                PurchasableItem(
                    "Pacote de Pinturas das Equipes",
                    "$12.99",
                    "Liveries oficiais com cores e logotipos das equipas.",
                    R.drawable.pacotedasequipes,
                    imageUrl = null
                ),
                PurchasableItem(
                    "Expansão de Carros Clássicos",
                    "$9.99",
                    "Carros lendários de eras históricas da F1.",
                    R.drawable.carroclassico,
                    imageUrl = null
                ),
                PurchasableItem(
                    "DLC de Circuitos Históricos",
                    "$14.99",
                    "Pistas icónicas com traçados clássicos.",
                    R.drawable.dlcdecircuito,
                    imageUrl = null
                )
            ),
            coverUrl = null,
            coverRes = R.drawable.formula01
        )
        GameDetailScreen(game = g, onBack = {})
    }
}
