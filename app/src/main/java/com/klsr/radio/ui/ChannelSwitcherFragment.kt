package com.klsr.radio.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.klsr.radio.R
import com.klsr.radio.RadioService

class ChannelSwitcherFragment(private val onSelected: (Int) -> Unit) : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val root = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        RadioService.STATIONS.forEachIndexed { index, station ->
            val tv = TextView(requireContext()).apply {
                text = station.name
                setPadding(0, 16, 0, 16)
                setTextAppearance(R.style.TextAppearance_MaterialComponents_Subtitle1)
                setOnClickListener {
                    onSelected(index)
                    dismiss()
                }
            }
            root.addView(tv)
        }
        dialog.setContentView(root)
        return dialog
    }
}
