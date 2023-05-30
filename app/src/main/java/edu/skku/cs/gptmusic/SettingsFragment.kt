package edu.skku.cs.gptmusic

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment(
    val email: String
) : Fragment(R.layout.fragment_settings) {
    var settingsFields = ArrayList<SettingsField>(0)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, null)

        // show user email
        val userEmail = view.findViewById<TextView>(R.id.textViewEmail)
        userEmail.text = email

        // add settings fields
        settingsFields.add(SettingsField("Logout"))

        // set list adapter
        val settingsList = view.findViewById<ListView>(R.id.settingsList)
        settingsList.adapter = SettingsAdapter(requireContext(), settingsFields)

        // add on click listener
        settingsList.setOnItemClickListener { parent, view, position, id ->
            // last option should be logout
            if(position == settingsFields.count() - 1){
                logoutBtn()
                println("Click Logout")
            }
        }

        return view
    }

    fun logoutBtn(){
        FirebaseAuth.getInstance().signOut()
        val loginIntent = Intent(context, LoginActivity::class.java)
        startActivity(loginIntent)
        requireActivity().finish()
    }
}