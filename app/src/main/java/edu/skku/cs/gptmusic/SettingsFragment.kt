package edu.skku.cs.gptmusic

import android.widget.TextView
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    fun setUserInfo(email: String) {
        val userEmail = view?.findViewById<TextView>(R.id.textViewEmail)
        userEmail?.text = email
    }
}