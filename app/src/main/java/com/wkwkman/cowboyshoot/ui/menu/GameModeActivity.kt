package com.wkwkman.cowboyshoot.ui.menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.wkwkman.cowboyshoot.R
import com.wkwkman.cowboyshoot.databinding.ActivityGameModeBinding
import com.wkwkman.cowboyshoot.ui.game.GameActivity
import com.wkwkman.cowboyshoot.ui.onboarding.entername.EnterNameFragment

class GameModeActivity : AppCompatActivity() {
    private val binding: ActivityGameModeBinding by lazy {
        ActivityGameModeBinding.inflate(layoutInflater)
    }
    
    private fun getUserName() {
        val profileName = intent.getStringExtra("Username")
        val welcomeMessage = "welcome, $profileName!"
        binding.tvPlayerWelcome.text = welcomeMessage
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()
        getUserName()
        getButtonResponses()
    }
    
    private fun getButtonResponses() {
        binding.llModePvc.setOnClickListener {
            val pvcSelectedMessage = "Current Game Mode:\nPlayer VS Computer"
            Toast.makeText(this@GameModeActivity, pvcSelectedMessage, Toast.LENGTH_SHORT).show()
            GameActivity.startActivity(this, false)
        }
        
        binding.llModePvp.setOnClickListener {
            val pvpSelectedMessage = "Current Game Mode:\nPlayer VS Player"
            Toast.makeText(this@GameModeActivity, pvpSelectedMessage, Toast.LENGTH_SHORT).show()
            GameActivity.startActivity(this, true)
        }
    }
}