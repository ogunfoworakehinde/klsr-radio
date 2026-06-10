package com.klsr.radio.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class SafeFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {
    abstract fun onSafeViewCreated(view: View, savedInstanceState: Bundle?)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            onSafeViewCreated(view, savedInstanceState)
        } catch (e: Exception) {
            // Inflate an error view to prevent crash
            val context = context ?: return
            val errorView = TextView(context).apply {
                text = "Error loading page: ${e.message}"
                setTextColor(0xFFFFFFFF.toInt())
                setPadding(32, 32, 32, 32)
            }
            (view as? ViewGroup)?.removeAllViews()
            (view as? ViewGroup)?.addView(errorView)
        }
    }
}
