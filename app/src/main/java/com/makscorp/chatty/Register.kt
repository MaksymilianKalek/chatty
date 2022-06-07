package com.makscorp.chatty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

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

            register(username, email, password)

        }
    }

    private fun register(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    addUserToDb(username, email, auth.currentUser?.uid!!)
                    val intent = Intent(this@Register, MainActivity::class.java);
                    finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(this@Register, "Some error", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserToDb(username: String, email: String, uid: String) {
        db = FirebaseDatabase.getInstance("https://chatty-400fc-default-rtdb.europe-west1.firebasedatabase.app").reference
        db.child("users").child(uid).setValue(User(username, email, uid))
    }


}