package com.klsr.radio.utils

import android.content.Context
import android.widget.ImageView
import android.graphics.drawable.ColorDrawable
import android.graphics.Color
import androidx.core.content.ContextCompat

object SafeImageHelper {
    fun load(context: Context?, imageView: ImageView?, drawableResId: Int) {
        if (imageView == null || context == null) return
        try {
            val drawable = ContextCompat.getDrawable(context, drawableResId)
            if (drawable != null) {
                imageView.setImageDrawable(drawable)
            } else {
                imageView.setImageDrawable(ColorDrawable(Color.GRAY))
            }
        } catch (e: Exception) {
            imageView.setImageDrawable(ColorDrawable(Color.GRAY))
        }
    }
}
