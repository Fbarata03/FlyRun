package com.example.flyrun.controller

import android.app.Activity
import android.content.Intent
import com.example.flyrun.model.Game
import com.example.flyrun.view.GameDetailActivity

object Navigator {
    const val EXTRA_GAME = "extra_game"

    fun openGame(activity: Activity, game: Game) {
        val intent = Intent(activity, GameDetailActivity::class.java)
        intent.putExtra(EXTRA_GAME, game)
        activity.startActivity(intent)
    }
}

