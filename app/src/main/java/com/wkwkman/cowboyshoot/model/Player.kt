package com.wkwkman.cowboyshoot.model

import com.wkwkman.cowboyshoot.enum.PlayerPosition
import com.wkwkman.cowboyshoot.enum.PlayerSide
import com.wkwkman.cowboyshoot.enum.PlayerState

data class Player(
    val playerSide: PlayerSide,
    var playerState: PlayerState,
    var playerPosition: PlayerPosition
)
