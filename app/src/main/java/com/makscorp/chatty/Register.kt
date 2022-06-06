package com.makscorp.chatty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {

    private lateinit var edtUsername: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnRegister: Button

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        edtUsername = findViewById(R.id.usernameInput)
        edtEmail = findViewById(R.id.emailInput)
        edtPassword = findViewById(R.id.passwordInput)
        btnRegister = findViewById(R.id.registerBtn)

        btnRegister.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            register(email, password)

        }
    }

    private fun register(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                println("TEST")
                println(task.exception?.message)
                println(task.result.toString())
                if (task.isSuccessful) {
                    println("TEST1")
                    startActivity(Intent(this@Register, MainActivity::class.java))

                } else {
                    println("TES2")
                    Toast.makeText(this@Register, "Some error", Toast.LENGTH_SHORT).show()
                }
            }
    }

}