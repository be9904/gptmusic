package edu.skku.cs.gptmusic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import edu.skku.cs.gptmusic.api.APIHandler
import edu.skku.cs.gptmusic.gpt.GPTFragment
import edu.skku.cs.gptmusic.home.HomeFragment
import edu.skku.cs.gptmusic.search.SearchFragment
import edu.skku.cs.gptmusic.settings.SettingsFragment

class HomeActivity : AppCompatActivity() {
    companion object{
        val fragment1 = HomeFragment()
        val fragment2 = GPTFragment()
        val fragment3 = SearchFragment()
        val fragment4 = SettingsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // check login
        val mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        if(user == null){
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }
        else{
            println("login successful, entering home activity")
        }

        // fragments
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.selectedItemId = R.id.home
        setFragment(fragment1)

        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home ->setFragment(fragment1)
                R.id.gpt ->setFragment(fragment2)
                R.id.search ->setFragment(fragment3)
                R.id.settings ->setFragment(fragment4)
            }
            true
        }

        // user should not be null
        APIHandler.main.initialFetch(user?.email.toString())
    }

    fun setFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }
}