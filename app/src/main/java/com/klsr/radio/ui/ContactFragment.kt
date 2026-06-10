package com.klsr.radio.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.klsr.radio.R
import com.klsr.radio.databinding.FragmentContactBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ContactFragment : Fragment(R.layout.fragment_contact) {
    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentContactBinding.bind(view)

        binding.sendContactBtn.setOnClickListener {
            val name = binding.contactNameEditText.text.toString()
            val email = binding.contactEmailEditText.text.toString()
            val subject = binding.contactSubjectEditText.text.toString()
            val message = binding.contactMessageEditText.text.toString()
            if (name.isBlank() || email.isBlank() || message.isBlank()) {
                Toast.makeText(requireContext(), "Please fill required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sendMessage(name, email, subject, message)
        }
    }

    private fun sendMessage(name: String, email: String, subject: String, message: String) {
        lifecycleScope.launch {
            val success = withContext(Dispatchers.IO) {
                try {
                    val json = JSONObject().apply {
                        put("service_id", "service_bg462kr")
                        put("template_id", "template_6pyapsf")
                        put("user_id", "835HY_wRkYBhqdu7d")
                        put("template_params", JSONObject().apply {
                            put("to_email", "robinlaura101@gmail.com")
                            put("from_name", name)
                            put("from_email", email)
                            put("subject", subject)
                            put("message", message)
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
                Toast.makeText(requireContext(), "Message sent!", Toast.LENGTH_SHORT).show()
                binding.contactNameEditText.text?.clear()
                binding.contactEmailEditText.text?.clear()
                binding.contactSubjectEditText.text?.clear()
                binding.contactMessageEditText.text?.clear()
            } else {
                Toast.makeText(requireContext(), "Failed, try again later", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
