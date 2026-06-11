package com.klsr.radio

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val circle1 = findViewById<ImageView>(R.id.circle1)
        val circle2 = findViewById<ImageView>(R.id.circle2)
        val circle3 = findViewById<ImageView>(R.id.circle3)
        val circle4 = findViewById<ImageView>(R.id.circle4)

        val pulseAnim = AnimationUtils.loadAnimation(this, R.anim.pulse_circle)

        // Stagger the start of each circle to create a "breathing" wave
        circle1.visibility = View.VISIBLE
        circle2.visibility = View.VISIBLE
        circle3.visibility = View.VISIBLE
        circle4.visibility = View.VISIBLE

        circle1.startAnimation(pulseAnim)
        circle2.startAnimation(pulseAnim.apply { startOffset = 300 })
        circle3.startAnimation(pulseAnim.apply { startOffset = 600 })
        circle4.startAnimation(pulseAnim.apply { startOffset = 900 })

        // Transition to MainActivity after 3 seconds
        window.decorView.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3500)
    }
}
