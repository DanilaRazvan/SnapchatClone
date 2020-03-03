package com.example.snapchat.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.snapchat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        if (mAuth.currentUser != null) {
            logIn()
        }
    }

    fun goClicked(v: View) {
        // Check if we can log in the user
        mAuth.signInWithEmailAndPassword(
            emailEditText.text.toString(),
            passwordEditText.text.toString()
        )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    logIn()
                } else {
                    // Sign up the user
                    mAuth.createUserWithEmailAndPassword(
                        emailEditText.text.toString(),
                        passwordEditText.text.toString()
                    ).addOnCompleteListener(this) { _ ->
                        if (task.isSuccessful) {
                            FirebaseDatabase.getInstance().reference.child("users")
                                .child(task.result?.user?.uid!!).child("email")
                                .setValue(emailEditText.text.toString())
                            logIn()
                        } else {
                            Toast.makeText(this, "Login Failed. Try Again.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
    }

    private fun logIn() {
        val intent = Intent(this, SnapsActivity::class.java)
        startActivity(intent)
    }
}
