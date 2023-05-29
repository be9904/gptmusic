package edu.skku.cs.gptmusic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import okhttp3.*

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        val id = findViewById<EditText>(R.id.editTextUsername)
        val pw = findViewById<EditText>(R.id.editTextPassword)

        if(tryLogin(id.text.toString(), pw.text.toString())) {

        }
    }

    fun tryLogin(id: String, pw: String): Boolean{
        return false
    }

    fun tryRegister(id: String, pw: String): Boolean{
        return false
    }

    fun handleRequest(host: String, path: String){
        val client = OkHttpClient()
    }
}