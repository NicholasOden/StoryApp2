package com.example.picodiploma.storyapp.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.picodiploma.storyapp.R
import com.example.picodiploma.storyapp.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    companion object {
        private const val SPLASH_DELAY = 3000L // Delay time in milliseconds
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Rotate animation
        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate)
        binding.imageViewSplash.startAnimation(rotateAnimation)

        // Delay and start login activity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        }, SPLASH_DELAY)
    }
}
