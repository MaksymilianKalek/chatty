package com.makscorp.chatty

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        edtEmail = findViewById(R.id.emailInput)
        edtPassword = findViewById(R.id.passwordInput)
        btnLogin = findViewById(R.id.loginBtn)
        btnRegister = findViewById(R.id.registerBtn)

        val sharedPref: SharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
        val savedEmail = sharedPref.getString("email", "")!!
        val savedPassword = sharedPref.getString("password", "")!!
        if (savedEmail.isNotEmpty() && savedPassword.isNotEmpty()) {
            login(savedEmail, savedPassword)
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            login(email, password)
        }
    }

    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val sharedPref: SharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
                    sharedPref.edit().remove("email").remove("password").putString("email", email).putString("password", password).apply()
                    val intent = Intent(this@Login, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(this@Login, "Incorrect user or password", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
}