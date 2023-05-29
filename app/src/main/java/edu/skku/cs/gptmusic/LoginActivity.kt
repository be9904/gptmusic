package edu.skku.cs.gptmusic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val homeIntent = Intent(this, HomeActivity::class.java).apply{}
            startActivity(homeIntent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        val editTextUsername = findViewById<EditText>(R.id.editTextUsername)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)

        val loginBtn = findViewById<Button>(R.id.buttonLogin)
        val registerBtn = findViewById<Button>(R.id.buttonRegister)
        val progressBar = findViewById<ProgressBar>(R.id.loginProgressBar)

        loginBtn.setOnClickListener{
            progressBar.visibility = View.VISIBLE

            val email = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()

            if(email.isEmpty())
                Toast.makeText(applicationContext, "Enter Email", Toast.LENGTH_SHORT).show()
            else if(password.isEmpty())
                Toast.makeText(applicationContext, "Enter Password", Toast.LENGTH_SHORT).show()

            // login, snippet from google firebase docs
            // https://firebase.google.com/docs/auth/android/password-auth
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        progressBar.visibility = View.GONE

                        // Sign in success, update UI with the signed-in user's information
                        println("signInWithEmail:success")
                        val homeIntent = Intent(this, HomeActivity::class.java).apply{}
                        startActivity(homeIntent)
                        finish()
                    } else {
                        progressBar.visibility = View.GONE

                        // If sign in fails, display a message to the user.
                        println("signInWithEmail:failure")
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }

        registerBtn.setOnClickListener{
            val registerIntent = Intent(this, RegisterActivity::class.java).apply{}
            startActivity(registerIntent)
        }
    }
}