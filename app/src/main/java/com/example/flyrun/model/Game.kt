package com.example.flyrun.model

import com.example.flyrun.R
import java.io.Serializable

data class PurchasableItem(
    val name: String,
    val price: String,
    val description: String,
    val iconRes: Int,
    val imageUrl: String? = null
) : Serializable

enum class GameId { F1, MotoGP }

data class Game(
    val id: GameId,
    val title: String,
    val description: String,
    val items: List<PurchasableItem>,
    val coverUrl: String? = null
) : Serializable

object GameRepository
