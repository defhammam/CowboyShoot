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
    fun onPlayerStatusChanged(playerOne: Player, iconDrawableRes: Int)
    fun onGameStateChanged(gameState: GameState)
    fun onGameFinished(gameState: GameState, theWinner: Player)
}

open class CowboyGameManagerImpl(private val listener: CowboyGameListener): CowboyGameManager {
    protected lateinit var playerOne: Player
    protected lateinit var playerTwo: Player
    protected lateinit var state: GameState

    override fun initGame() {
        setGameState(GameState.IDLE)
        playerOne = Player(PlayerSide.PLAYER_ONE, PlayerState.IDLE, PlayerPosition.MIDDLE)
        playerTwo = Player(PlayerSide.PLAYER_ONE, PlayerState.IDLE, PlayerPosition.MIDDLE)
        notifyPlayerDataChanged()
        setGameState(GameState.STARTED)
    }

    fun setGameState(newGameState: GameState) {
        state = newGameState
        listener.onGameStateChanged(state)
    }

    private fun notifyPlayerDataChanged() {
        listener.onPlayerStatusChanged(
            playerOne,
            getPlayerDrawableByState(playerOne.playerState)
        )
        listener.onPlayerStatusChanged(
            playerTwo,
            getPlayerTwoDrawableByState(playerTwo.playerState)
        )
    }

    override fun movePlayerUpward() {
        if (state != GameState.FINISHED && playerOne.playerPosition.ordinal > PlayerPosition.TOP.ordinal) {
            val currentIndex = playerOne.playerPosition.ordinal
            setPlayerOneMovement(getPlayerPositionByOrdinal(currentIndex - 1), PlayerState.IDLE)
        }
    }

    override fun movePlayerDownward() {
        if (state != GameState.FINISHED && playerOne.playerPosition.ordinal < PlayerPosition.BOTTOM.ordinal) {
            val currentIndex = playerOne.playerPosition.ordinal
            setPlayerOneMovement(getPlayerPositionByOrdinal(currentIndex + 1), PlayerState.IDLE)
        }
    }

    protected fun setPlayerOneMovement(
        newPosition: PlayerPosition = playerOne.playerPosition,
        newState: PlayerState = playerOne.playerState): Unit {
        playerOne.apply {
            this.playerPosition = newPosition
            this.playerState = newState
        }
        listener.onPlayerStatusChanged(
            playerOne,
            getPlayerDrawableByState(playerOne.playerState)
        )
    }

    private fun setPlayerTwoMovement(
        newPosition: PlayerPosition = playerTwo.playerPosition,
        newState: PlayerState = playerTwo.playerState): Unit {
        playerTwo.apply {
            this.playerPosition = newPosition
            this.playerState = newState
        }
        listener.onPlayerStatusChanged(
            playerTwo,
            getPlayerTwoDrawableByState(playerTwo.playerState)
        )
    }

    protected fun getPlayerPositionByOrdinal(index: Int): PlayerPosition {
        return PlayerPosition.values()[index]
    }

    private fun getPlayerDrawableByState(playerState: PlayerState): Int {
        return when (playerState) {
            PlayerState.IDLE -> R.drawable.ic_cowboy_left_shoot_false
            PlayerState.SHOOT -> R.drawable.ic_cowboy_left_shoot_true
            PlayerState.DEAD -> R.drawable.ic_cowboy_left_dead
        }
    }

    private fun getPlayerTwoDrawableByState(playerState: PlayerState): Int {
        return when (playerState) {
            PlayerState.IDLE -> R.drawable.ic_cowboy_right_shoot_false
            PlayerState.SHOOT -> R.drawable.ic_cowboy_right_shoot_true
            PlayerState.DEAD -> R.drawable.ic_cowboy_right_dead
        }
    }

    override fun startOrRestartGame() {
        if (state != GameState.FINISHED) {
            startGame()
        } else {
            // The game is restarted
            initGame()
        }
    }
    
    protected fun startGame(): Unit {
        playerTwo.apply {
            playerPosition = getPlayerTwoPosition()
        }
        checkWinner()
    }

    open fun getPlayerTwoPosition(): PlayerPosition {
        val randomPosition = Random.nextInt(0, until = PlayerPosition.values().size)
        return getPlayerPositionByOrdinal(randomPosition)
    }

    private fun checkWinner() {
        val winner = if (playerOne.playerPosition == playerTwo.playerPosition) {
            setPlayerOneMovement(newState = PlayerState.DEAD)
            setPlayerTwoMovement(newState = PlayerState.SHOOT)
            playerOne
        } else {
            setPlayerOneMovement(newState = PlayerState.SHOOT)
            setPlayerTwoMovement(newState = PlayerState.DEAD)
            playerTwo
        }
        setGameState(GameState.FINISHED)
        listener.onGameFinished(state, winner)
    }
}

class MultiplayerCowboyGameManager(listener: CowboyGameListener):CowboyGameManagerImpl(listener) {
    override fun initGame() {
        super.initGame()
        setGameState(GameState.TURN_PLAYER_ONE)
    }

    override fun getPlayerTwoPosition(): PlayerPosition {
        return playerTwo.playerPosition
    }

    override fun movePlayerUpward() {
        if (state == GameState.TURN_PLAYER_ONE) {
            super.movePlayerUpward()
        } else if (state == GameState.TURN_PLAYER_TWO) {
            if (playerTwo.playerPosition.ordinal > PlayerPosition.TOP.ordinal) {
                val currentIndex = playerTwo.playerPosition.ordinal
                setPlayerOneMovement(getPlayerPositionByOrdinal(currentIndex - 1), PlayerState.IDLE)
            }
        }
    }

    override fun movePlayerDownward() {
        if (state == GameState.TURN_PLAYER_ONE) {
            super.movePlayerDownward()
        } else if (state == GameState.TURN_PLAYER_TWO) {
            if (playerTwo.playerPosition.ordinal < PlayerPosition.BOTTOM.ordinal) {
                val currentIndex = playerTwo.playerPosition.ordinal
                setPlayerOneMovement(getPlayerPositionByOrdinal(currentIndex + 1), PlayerState.IDLE)
            }
        }
    }

    override fun startOrRestartGame() {
        when (state) {
            GameState.TURN_PLAYER_ONE -> {
                setGameState(GameState.TURN_PLAYER_TWO)
            }
            GameState.TURN_PLAYER_TWO -> startGame()
            GameState.FINISHED -> initGame()
            else -> return
        }
    }
}