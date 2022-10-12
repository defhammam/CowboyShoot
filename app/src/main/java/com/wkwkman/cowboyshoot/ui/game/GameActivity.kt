package com.wkwkman.cowboyshoot.ui.game

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.wkwkman.cowboyshoot.R
import com.wkwkman.cowboyshoot.databinding.ActivityGameBinding
import com.wkwkman.cowboyshoot.enum.GameState
import com.wkwkman.cowboyshoot.enum.PlayerPosition
import com.wkwkman.cowboyshoot.enum.PlayerSide
import com.wkwkman.cowboyshoot.manager.CowboyGameManagerImpl
import com.wkwkman.cowboyshoot.manager.CowboyGameListener
import com.wkwkman.cowboyshoot.manager.CowboyGameManager
import com.wkwkman.cowboyshoot.manager.MultiplayerCowboyGameManager
import com.wkwkman.cowboyshoot.model.Player

class GameActivity: AppCompatActivity(), CowboyGameListener {
    private val binding: ActivityGameBinding by lazy {
        ActivityGameBinding.inflate(layoutInflater)
    }
    private val isUsingMultiplayerMode: Boolean by lazy {
        intent.getBooleanExtra(EXTRAS_MULTIPLAYER_MODE, false)
    }
    private val cowboyGameManager: CowboyGameManager by lazy {
        if (isUsingMultiplayerMode)
            MultiplayerCowboyGameManager(this)
        else
            CowboyGameManagerImpl(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        cowboyGameManager.initGame()
        setOnClickListeners()
        supportActionBar?.hide()
    }
    private fun setOnClickListeners() {
        binding.ivArrowUp.setOnClickListener {
            cowboyGameManager.movePlayerUpward()
        }
        binding.ivArrowDown.setOnClickListener {
            cowboyGameManager.movePlayerDownward()
        }
        binding.tvActionGame.setOnClickListener {
            cowboyGameManager.startOrRestartGame()
        }
    }
    private fun setCharacterMovement(player: Player, iconDrawableRes: Int) {
        val ivCharTop: ImageView?
        val ivCharMiddle: ImageView?
        val ivCharBottom: ImageView?
        val drawable = ContextCompat.getDrawable(this, iconDrawableRes)

        if (player.playerSide == PlayerSide.PLAYER_ONE) {
            ivCharTop = binding.ivPlayerTop
            ivCharMiddle = binding.ivPlayerMiddle
            ivCharBottom = binding.ivPlayerBottom
        } else {
            ivCharTop = binding.ivBotTop
            ivCharMiddle = binding.ivBotMiddle
            ivCharBottom = binding.ivBotBottom
        }

        when (player.playerPosition) {
            PlayerPosition.TOP -> {
                ivCharTop.visibility = View.VISIBLE
                ivCharMiddle.visibility = View.INVISIBLE
                ivCharBottom.visibility = View.INVISIBLE
                ivCharTop.setImageDrawable(drawable)
            } PlayerPosition.MIDDLE -> {
                ivCharTop.visibility = View.INVISIBLE
                ivCharMiddle.visibility = View.VISIBLE
                ivCharBottom.visibility = View.INVISIBLE
                ivCharMiddle.setImageDrawable(drawable)
            } PlayerPosition.BOTTOM -> {
                ivCharTop.visibility = View.INVISIBLE
                ivCharMiddle.visibility = View.INVISIBLE
                ivCharBottom.visibility = View.VISIBLE
                ivCharBottom.setImageDrawable(drawable)
            }
        }
    }
    override fun onPlayerStatusChanged(player: Player, iconDrawableRes: Int) {
        setCharacterMovement(player, iconDrawableRes)
    }
    override fun onGameStateChanged(gameState: GameState) {
        binding.tvStatusGame.text = ""
        when (gameState) {
            GameState.IDLE -> {
                binding.tvActionGame.text = getString(R.string.text_fire)
                setCharacterVisibility(isPlayerOneVisible = true, isPlayerTwoVisible = true)
            }
            GameState.STARTED -> {
                binding.tvActionGame.text = getString(R.string.text_fire)
                setCharacterVisibility(isPlayerOneVisible = true, isPlayerTwoVisible = true)
            }
            GameState.FINISHED -> {
                binding.tvActionGame.text = getString(R.string.text_restart)
                setCharacterVisibility(isPlayerOneVisible = true, isPlayerTwoVisible = true)
            }
            GameState.TURN_PLAYER_ONE -> {
                binding.tvActionGame.text = getString(R.string.text_lock_player)
                setCharacterVisibility(isPlayerOneVisible = true, isPlayerTwoVisible = false)
            }
            GameState.TURN_PLAYER_TWO -> {
                binding.tvActionGame.text = getString(R.string.text_fire)
                setCharacterVisibility(isPlayerOneVisible = false, isPlayerTwoVisible = true)
            }
        }
    }
    private fun setCharacterVisibility(isPlayerOneVisible: Boolean, isPlayerTwoVisible: Boolean) {
        binding.llPlayer.isVisible = isPlayerOneVisible
        binding.llBot.isVisible = isPlayerTwoVisible
    }
    override fun onGameFinished(gameState: GameState, theWinner: Player) {
        if (theWinner.playerSide == PlayerSide.PLAYER_ONE) {
            binding.tvStatusGame.text = getString(R.string.text_you_win)
        } else {
            binding.tvStatusGame.text = getString(R.string.text_you_lose)
        }
    }
    companion object {
        private const val EXTRAS_MULTIPLAYER_MODE = "EXTRAS_MULTIPLAYER_MODE"
        fun startActivity(context: Context, isMultiplayer: Boolean) {
            context.startActivity(Intent(context, GameActivity::class.java).apply {
                putExtra(EXTRAS_MULTIPLAYER_MODE, isMultiplayer)
            })
        }
    }
}