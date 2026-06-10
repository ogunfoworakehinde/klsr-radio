package com.klsr.radio.ui

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController

class MoreMenuDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val items = arrayOf("About Us", "Donate", "Contact", "Settings")
        val destinations = intArrayOf(
            R.id.aboutFragment,
            R.id.donationFragment,
            R.id.contactFragment,
            R.id.settingsFragment
        )
        return AlertDialog.Builder(requireContext())
            .setTitle("More")
            .setItems(items) { _, which ->
                findNavController().navigate(destinations[which])
            }
            .create()
    }
}
