package com.example.constructora

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val correo: EditText = findViewById(R.id.correo)
        val pass: EditText = findViewById(R.id.password)
        val sesion: Button = findViewById(R.id.button_sesion)

        firebaseAuth = Firebase.auth

        sesion.setOnClickListener {
            signIn(correo.text.toString(), pass.text.toString())
        }
    }
    private fun signIn(correo: String, pass: String) {
        firebaseAuth.signInWithEmailAndPassword(correo, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Autenticación Correcta", Toast.LENGTH_SHORT).show()
                    val user= Intent(this, MainActivity2::class.java)
                    startActivity(user)
                } else {
                    Toast.makeText(baseContext, "Autenticación Fallida", Toast.LENGTH_SHORT).show()
                }
            }
    }
}