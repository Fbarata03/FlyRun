@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.flyrun.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import com.example.flyrun.R
import com.example.flyrun.controller.Navigator
import com.example.flyrun.model.Game
import com.example.flyrun.model.GameId
import com.example.flyrun.model.PurchasableItem

import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.platform.LocalInspectionMode
import com.example.flyrun.ui.theme.DarkPreview
import com.example.flyrun.ui.theme.FlyRunTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlyRunTheme(dark = true) {
                val games = sampleGames()
                MainScreen(games = games, onOpenGame = { game -> Navigator.openGame(this, game) })
            }
        }
    }
}

@Composable
fun MainScreen(games: List<Game>, onOpenGame: (Game) -> Unit) {
    val selectedTab = remember { mutableIntStateOf(0) }
    Scaffold(
        topBar = { FlyRunTopBar() },
        bottomBar = { FlyRunBottomNav(selectedTab.value) { selectedTab.value = it } },
        content = { padding ->
            if (selectedTab.value == 0) {
                GamesList(Modifier.padding(padding), games, onOpenGame)
            } else {
                PlaceholderTab(Modifier.padding(padding))
            }
        }
    )
}

@Composable
fun GamesList(modifier: Modifier, games: List<Game>, onOpenGame: (Game) -> Unit) {
    val cards = listOf(
        GameCardData(
            title = games[0].title,
            gradient = Brush.linearGradient(listOf(Color(0xFF3A3A3A), Color(0xFFC80000))),
            iconRes = R.drawable.ic_badge_f1,
            game = games[0]
        ),
        GameCardData(
            title = games[1].title,
            gradient = Brush.linearGradient(listOf(Color(0xFF2A2A2A), Color(0xFF0055A4))),
            iconRes = R.drawable.ic_badge_motogp,
            game = games[1]
        )
    )
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 64.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(cards) { c ->
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                GameCard(c, onOpenGame)
            }
        }
    }
}

data class GameCardData(
    val title: String,
    val gradient: Brush,
    val iconRes: Int,
    val game: Game
)

@Composable
fun GameCard(data: GameCardData, onOpenGame: (Game) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(180.dp)
            .padding(horizontal = 16.dp)
            .clickable { onOpenGame(data.game) },
        shape = CardDefaults.shape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                data.game.coverRes != null -> {
                    Image(
                        painter = painterResource(data.game.coverRes),
                        contentDescription = data.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                data.game.coverUrl != null -> {
                    AsyncImage(
                        model = data.game.coverUrl,
                        contentDescription = data.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(data.gradient)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(data.iconRes),
                    contentDescription = data.title,
                    tint = Color.Unspecified
                )
            }
            Text(
                text = data.title,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AsyncImage(
    model: String,
    contentDescription: String,
    modifier: Modifier,
    contentScale: ContentScale
) {
    val bitmapState = remember { mutableStateOf<Bitmap?>(null) }
    val isPreview = LocalInspectionMode.current

    LaunchedEffect(model, isPreview) {
        if (isPreview) {
            bitmapState.value = null
        } else {
            val url = model
            bitmapState.value = try {
                withContext(Dispatchers.IO) {
                    val connection = URL(url).openConnection() as HttpURLConnection
                    connection.connectTimeout = 5000
                    connection.readTimeout = 5000
                    connection.instanceFollowRedirects = true
                    connection.inputStream.use { stream ->
                        BitmapFactory.decodeStream(stream)
                    }
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    val bmp = bitmapState.value
    if (bmp != null) {
        Image(
            bitmap = bmp.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        Box(modifier = modifier.background(Color.Black.copy(alpha = 0.1f)))
    }
}

@Composable
fun FlyRunTopBar() {
    TopAppBar(
        title = { Text("FlyRun", style = MaterialTheme.typography.titleLarge) },
        actions = {
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(32.dp)
                    .background(Color.White.copy(alpha = 0.2f), RectangleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(R.drawable.ic_bell),
                    contentDescription = "Notificações",
                    tint = Color.White
                )
            }
            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(32.dp)
                    .background(Color.White.copy(alpha = 0.2f), RectangleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(R.drawable.ic_settings),
                    contentDescription = "Configurações",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
fun FlyRunBottomNav(selectedIndex: Int, onSelect: (Int) -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
        NavigationBarItem(
            selected = selectedIndex == 0,
            onClick = { onSelect(0) },
            icon = { Icon(painterResource(R.drawable.ic_star), contentDescription = "Featured") },
            label = { Text("Featured") }
        )
        NavigationBarItem(
            selected = selectedIndex == 1,
            onClick = { },
            enabled = false,
            icon = { Icon(painterResource(R.drawable.ic_clock), contentDescription = "History") },
            label = { Text("History") }
        )
        NavigationBarItem(
            selected = selectedIndex == 2,
            onClick = { },
            enabled = false,
            icon = { Icon(painterResource(R.drawable.ic_user), contentDescription = "Profile") },
            label = { Text("Profile") }
        )
    }
}

private fun sampleGames(): List<Game> {
    val f1Items = listOf(
        PurchasableItem(
            name = "Pacote de Pinturas das Equipes",
            price = "$12.99",
            description = "Liveries oficiais com cores e logotipos das equipas.",
            iconRes = R.drawable.pacotedasequipes,
            imageUrl = null
        ),
        PurchasableItem(
            name = "Expansão de Carros Clássicos",
            price = "$9.99",
            description = "Carros lendários de eras históricas da F1.",
            iconRes = R.drawable.carroclassico,
            imageUrl = null
        ),
        PurchasableItem(
            name = "DLC de Circuitos Históricos",
            price = "$14.99",
            description = "Pistas icónicas com traçados clássicos.",
            iconRes = R.drawable.dlcdecircuito,
            imageUrl = null
        )
    )

    val motoItems = listOf(
        PurchasableItem(
            name = "Pacote de Equipamento do Piloto",
            price = "$11.99",
            description = "Fato com marcação e patrocínios oficiais.",
            iconRes = R.drawable.pacotedeequipamento,
            imageUrl = null
        ),
        PurchasableItem(
            name = "Coleção de Motos Clássicas",
            price = "$8.99",
            description = "Motos vintage de temporadas memoráveis.",
            iconRes = R.drawable.colesaodemotos,
            imageUrl = null
        ),
        PurchasableItem(
            name = "Expansão de Circuitos do GP",
            price = "$15.99",
            description = "Circuitos emblemáticos de Grandes Prémios.",
            iconRes = R.drawable.expansaodo,
            imageUrl = null
        )
    )

    val f1 = Game(
        id = GameId.F1,
        title = "Fórmula 1®",
        description = "A official Formula 1 racing experience with all teams, drivers and circuits.",
        items = f1Items,
        coverUrl = null,
        coverRes = R.drawable.formula01
    )

    val moto = Game(
        id = GameId.MotoGP,
        title = "MotoGP™",
        description = "",
        items = motoItems,
        coverUrl = null,
        coverRes = R.drawable.motogp0
    )

    return listOf(f1, moto)
}


 

@DarkPreview
@Composable
fun PreviewMainScreen() {
    FlyRunTheme(dark = isSystemInDarkTheme()) {
        MainScreen(games = sampleGames(), onOpenGame = {})
    }
}




@DarkPreview
@Composable
fun PreviewTopBar() {
    FlyRunTheme(dark = isSystemInDarkTheme()) {
        FlyRunTopBar()
    }
}

@DarkPreview
@Composable
fun PreviewBottomNav() {
    FlyRunTheme(dark = isSystemInDarkTheme()) {
        FlyRunBottomNav(selectedIndex = 0, onSelect = {})
    }
}

@Composable
fun PlaceholderTab(modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Em breve", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Selecione Featured para ver a lista de jogos")
    }
}
