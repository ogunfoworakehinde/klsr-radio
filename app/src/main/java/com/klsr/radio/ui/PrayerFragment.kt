package com.klsr.radio.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.klsr.radio.R
import com.klsr.radio.databinding.FragmentPrayerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class PrayerFragment : Fragment(R.layout.fragment_prayer) {
    private var _binding: FragmentPrayerBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPrayerBinding.bind(view)

        binding.submitPrayerBtn.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val phone = binding.phoneEditText.text.toString()
            val request = binding.requestEditText.text.toString()
            if (name.isBlank() || request.isBlank()) {
                Toast.makeText(requireContext(), "Please fill required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            submitPrayer(name, email, phone, request)
        }
    }

    private fun submitPrayer(name: String, email: String, phone: String, request: String) {
        lifecycleScope.launch {
            val success = withContext(Dispatchers.IO) {
                try {
                    val json = JSONObject().apply {
                        put("service_id", "service_bg462kr")
                        put("template_id", "template_6pyapsf")
                        put("user_id", "835HY_wRkYBhqdu7d")
                        put("template_params", JSONObject().apply {
                            put("to_email", "support@kingdomlifestyleradio.com")
                            put("from_name", name)
                            put("from_email", email)
                            put("phone", phone)
                            put("message", request)
                            put("followup", binding.followupCheckBox.isChecked)
                        })
                    }
                    val url = URL("https://api.emailjs.com/api/v1.0/email/send")
                    val conn = url.openConnection() as HttpURLConnection
                    conn.doOutput = true
                    conn.setRequestProperty("Content-Type", "application/json")
                    conn.outputStream.write(json.toString().toByteArray())
                    conn.responseCode == 200
                } catch (e: Exception) {
                    false
                }
            }
            if (success) {
                Toast.makeText(requireContext(), "Prayer request sent!", Toast.LENGTH_SHORT).show()
                binding.nameEditText.text?.clear()
                binding.emailEditText.text?.clear()
                binding.phoneEditText.text?.clear()
                binding.requestEditText.text?.clear()
            } else {
                Toast.makeText(requireContext(), "Failed, but your prayer is heard.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
