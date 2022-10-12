package com.wkwkman.cowboyshoot.ui.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.wkwkman.cowboyshoot.R
import com.wkwkman.cowboyshoot.databinding.ActivityGameBinding
import com.wkwkman.cowboyshoot.enum.GameState
import com.wkwkman.cowboyshoot.enum.PlayerPosition
import com.wkwkman.cowboyshoot.enum.PlayerSide
import com.wkwkman.cowboyshoot.manager.ComputerCowboyGameManager
import com.wkwkman.cowboyshoot.manager.CowboyGameListener
import com.wkwkman.cowboyshoot.manager.CowboyGameManager
import com.wkwkman.cowboyshoot.model.Player

class GameActivity: AppCompatActivity(), CowboyGameListener {
    private val binding: ActivityGameBinding by lazy {
        ActivityGameBinding.inflate(layoutInflater)
    }
    private val cowboyGameManager: CowboyGameManager by lazy {
        ComputerCowboyGameManager(this)
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
        binding.tvActionGame.text = when (gameState) {
            GameState.IDLE -> getString(R.string.text_start)
            GameState.STARTED -> getString(R.string.text_fire)
            GameState.FINISHED -> getString(R.string.text_restart)
        }
    }
    override fun onGameFinished(gameState: GameState, theWinner: Player) {
        if (theWinner.playerSide == PlayerSide.PLAYER_ONE) {
            binding.tvStatusGame.text = getString(R.string.text_you_win)
        } else {
            binding.tvStatusGame.text = getString(R.string.text_you_lose)
        }
    }
}