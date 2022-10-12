package com.wkwkman.cowboyshoot.ui.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.wkwkman.cowboyshoot.R
import com.wkwkman.cowboyshoot.databinding.ActivityGameBinding
import com.wkwkman.cowboyshoot.databinding.ActivitySplashScreenBinding
import com.wkwkman.cowboyshoot.ui.onboarding.OnboardingActivity

class SplashScreenActivity: AppCompatActivity() {
    private val binding: ActivitySplashScreenBinding by lazy {
        ActivitySplashScreenBinding.inflate(layoutInflater)
    }
    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()
        startTimerSplashScreen()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Destroy timer in case the app is still running in the background ...
        // ... when user pressed home while the app is showing splash screen
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
    }

    private fun startTimerSplashScreen() {
        timer = object: CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                val intent = Intent(this@SplashScreenActivity, OnboardingActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        timer?.start()
    }
}