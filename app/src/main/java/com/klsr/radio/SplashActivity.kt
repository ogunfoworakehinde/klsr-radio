package com.klsr.radio

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val frontLayout = findViewById<View>(R.id.frontLayout)
        val backLayout = findViewById<View>(R.id.backLayout)
        val root = findViewById<FrameLayout>(R.id.splash_root)

        // Start flip after a short delay to let the logo be visible
        root.postDelayed({
            startBookOpenAnimation(frontLayout, backLayout)
        }, 1000) // 1 second delay to show the front cover
    }

    private fun startBookOpenAnimation(front: View, back: View) {
        val cameraDistance = 8000f // needed for 3D effect
        front.cameraDistance = cameraDistance
        back.cameraDistance = cameraDistance

        // Start with front visible, back invisible
        front.visibility = View.VISIBLE
        back.visibility = View.VISIBLE
        back.rotationY = 180f // initially flipped (hidden)

        val frontAnim = ObjectAnimator.ofFloat(front, "rotationY", 0f, 180f)
        val backAnim = ObjectAnimator.ofFloat(back, "rotationY", 180f, 360f)

        val set = AnimatorSet()
        set.playTogether(frontAnim, backAnim)
        set.duration = 1200
        set.interpolator = AccelerateDecelerateInterpolator()
        set.start()

        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // After flip, wait a moment then go to main
                front.postDelayed({
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }, 1000) // 1 second to read welcome text
            }
        })
    }
}
