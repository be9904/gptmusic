package edu.skku.cs.gptmusic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val mAuth = FirebaseAuth.getInstance()
        val logoutBtn = findViewById<Button>(R.id.buttonLogout)
        val textLog = findViewById<TextView>(R.id.textViewLog)

        // fragments
        val fragment1 = SettingsFragment()
        val fragment2 = SettingsFragment()
        val fragment3 = SettingsFragment()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.gpt->setFragment(fragment1)
                R.id.settings2->setFragment(fragment2)
                R.id.settings3->setFragment(fragment3)
            }
            true
        }

        // check login
        val user = mAuth.currentUser
        if(user == null){
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }
        else{
            textLog.text = "${user.email}"
        }

        logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }
    }

    fun setFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }
}