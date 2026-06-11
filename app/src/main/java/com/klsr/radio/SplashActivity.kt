package com.klsr.radio

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Go directly to MainActivity – no delay, no animation
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
