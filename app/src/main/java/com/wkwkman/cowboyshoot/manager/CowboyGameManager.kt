package com.wkwkman.cowboyshoot.manager

import com.wkwkman.cowboyshoot.R
import com.wkwkman.cowboyshoot.enum.GameState
import com.wkwkman.cowboyshoot.enum.PlayerPosition
import com.wkwkman.cowboyshoot.enum.PlayerSide
import com.wkwkman.cowboyshoot.enum.PlayerState
import com.wkwkman.cowboyshoot.model.Player
import kotlin.random.Random

interface CowboyGameManager {
    fun initGame()
    fun movePlayerUpward()
    fun movePlayerDownward()
    fun startOrRestartGame()
}

interface CowboyGameListener {
    fun onPlayerStatusChanged(player: Player, iconDrawableRes: Int)
    fun onGameStateChanged(gameState: GameState)
    fun onGameFinished(gameState: GameState, theWinner: Player)
}

class ComputerCowboyGameManager(private val listener: CowboyGameListener): CowboyGameManager {
    private lateinit var player: Player
    private lateinit var bot: Player
    private lateinit var gameState: GameState

    override fun initGame() {
        setGameState(GameState.IDLE)
        player = Player(PlayerSide.PLAYER_ONE, PlayerState.IDLE, PlayerPosition.MIDDLE)
        bot = Player(PlayerSide.PLAYER_ONE, PlayerState.IDLE, PlayerPosition.MIDDLE)
        notifyPlayerDataChanged()
        setGameState(GameState.STARTED)
    }

    private fun setGameState(newGameState: GameState) {
        gameState = newGameState
        listener.onGameStateChanged(gameState)
    }

    private fun notifyPlayerDataChanged() {
        listener.onPlayerStatusChanged(
            player,
            getPlayerDrawableByState(player.playerState)
        )
        listener.onPlayerStatusChanged(
            bot,
            getBotDrawableByState(bot.playerState)
        )
    }

    override fun movePlayerUpward() {
        if (gameState != GameState.FINISHED && player.playerPosition.ordinal > PlayerPosition.TOP.ordinal) {
            val currentIndex = player.playerPosition.ordinal
            setPlayerMovement(getPlayerPositionByOrdinal(currentIndex - 1), PlayerState.IDLE)
        }
    }

    override fun movePlayerDownward() {
        if (gameState != GameState.FINISHED && player.playerPosition.ordinal < PlayerPosition.BOTTOM.ordinal) {
            val currentIndex = player.playerPosition.ordinal
            setPlayerMovement(getPlayerPositionByOrdinal(currentIndex + 1), PlayerState.IDLE)
        }
    }

    private fun setPlayerMovement(
        newPosition: PlayerPosition = player.playerPosition,
        newState: PlayerState = player.playerState): Unit {
        player.apply {
            this.playerPosition = newPosition
            this.playerState = newState
        }
        listener.onPlayerStatusChanged(
            player,
            getPlayerDrawableByState(player.playerState)
        )
    }

    private fun setBotMovement(
        newPosition: PlayerPosition = bot.playerPosition,
        newState: PlayerState = bot.playerState): Unit {
        bot.apply {
            this.playerPosition = newPosition
            this.playerState = newState
        }
        listener.onPlayerStatusChanged(
            bot,
            getBotDrawableByState(bot.playerState)
        )
    }

    private fun getPlayerPositionByOrdinal(index: Int): PlayerPosition {
        return PlayerPosition.values()[index]
    }

    private fun getPlayerDrawableByState(playerState: PlayerState): Int {
        return when (playerState) {
            PlayerState.IDLE -> R.drawable.ic_cowboy_left_shoot_false
            PlayerState.SHOOT -> R.drawable.ic_cowboy_left_shoot_true
            PlayerState.DEAD -> R.drawable.ic_cowboy_left_dead
        }
    }

    private fun getBotDrawableByState(playerState: PlayerState): Int {
        return when (playerState) {
            PlayerState.IDLE -> R.drawable.ic_cowboy_right_shoot_false
            PlayerState.SHOOT -> R.drawable.ic_cowboy_right_shoot_true
            PlayerState.DEAD -> R.drawable.ic_cowboy_right_dead
        }
    }

    override fun startOrRestartGame() {
        if (gameState != GameState.FINISHED) {
            startGame()
        } else {
            // The game is restarted
            initGame()
        }
    }
    
    private fun startGame(): Unit {
        bot.apply {
            playerPosition = getBotPosition()
        }
        checkWinner()
    }

    private fun getBotPosition(): PlayerPosition {
        val randomPosition = Random.nextInt(0, until = PlayerPosition.values().size)
        return getPlayerPositionByOrdinal(randomPosition)
    }

    private fun checkWinner() {
        val winner = if (player.playerPosition == bot.playerPosition) {
            setPlayerMovement(newState = PlayerState.DEAD)
            setBotMovement(newState = PlayerState.SHOOT)
            player
        } else {
            setPlayerMovement(newState = PlayerState.SHOOT)
            setBotMovement(newState = PlayerState.DEAD)
            bot
        }
        setGameState(GameState.FINISHED)
        listener.onGameFinished(gameState, winner)
    }
}