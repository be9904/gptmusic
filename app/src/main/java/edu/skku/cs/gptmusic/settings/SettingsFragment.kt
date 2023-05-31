package edu.skku.cs.gptmusic.settings

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import edu.skku.cs.gptmusic.HomeActivity
import edu.skku.cs.gptmusic.LoginActivity
import edu.skku.cs.gptmusic.R
import edu.skku.cs.gptmusic.api.APIHandler

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

        // fetch key
        HomeActivity.apiHandler.fetchAPIKey(email)

        // add settings fields
        settingsFields.clear()
        settingsFields.add(SettingsField("Show API Key"))
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

            if(view.findViewById<TextView>(R.id.textViewField).text.toString() == "Show API Key"){
                showKeyDialog(email)
            }
        }

        // edit api key button
        val editKey = view.findViewById<ImageView>(R.id.editImageView)
        editKey.setOnClickListener{
            editKeyDialog()
        }

        return view
    }

    fun showKeyDialog(email: String) {
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_showkey, null)
        val textView = dialogLayout.findViewById<TextView>(R.id.textViewAPIKey)

        with(builder) {
            setTitle("API Key")
            textView.text = HomeActivity.apiHandler.apikey
            setPositiveButton("OK", null)
            setView(dialogLayout)
            show()
        }
    }

    fun editKeyDialog(){
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_keyedit, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editTextAPIKey)

        with(builder){
            setTitle("New API Key")
            setPositiveButton("OK"){dialog, which ->
                // send changes to firebase db
                println("New API Key: ${editText.text}")
            }
            setNegativeButton("Cancel"){dialog, which ->
                println("API Key update cancelled.")
            }
            setView(dialogLayout)
            show()
        }
    }

    fun logoutBtn(){
        FirebaseAuth.getInstance().signOut()
        val loginIntent = Intent(context, LoginActivity::class.java)
        startActivity(loginIntent)
        requireActivity().finish()
    }
}