package com.makscorp.chatty

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Register : AppCompatActivity() {

    private lateinit var edtUsername: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var db: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        edtUsername = findViewById(R.id.usernameInput)
        edtEmail = findViewById(R.id.emailInput)
        edtPassword = findViewById(R.id.passwordInput)
        btnRegister = findViewById(R.id.registerBtn)

        btnRegister.setOnClickListener {
            val username = edtUsername.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                if (password.length >= 8) {
                    register(username, email, password)
                } else {
                    Toast.makeText(
                        this@Register,
                        "Your password needs to be at least 8 characters long",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@Register,
                    "You need to fill all of the text fields",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun register(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    addUserToDb(username, email, auth.currentUser?.uid!!)
                    val intent = Intent(this@Register, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(this@Register, "Server error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun addUserToDb(username: String, email: String, uid: String) {
        db =
            FirebaseDatabase.getInstance("https://chatty-400fc-default-rtdb.europe-west1.firebasedatabase.app").reference
        db.child("users").child(uid).setValue(User(username, email, uid))
    }


}