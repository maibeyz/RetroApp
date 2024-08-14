package com.mai.retroapp.ui.viewmodel

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mai.retroapp.R
import com.mai.retroapp.databinding.ActivityJoinSessionBinding

class JoinSessionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinSessionBinding
    private lateinit var database: DatabaseReference
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_join_session)

        binding = ActivityJoinSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference
        username = intent.getStringExtra("USERNAME") ?: ""


        binding.buttonJoin.setOnClickListener {
            val sessionName = binding.editTextJoinSessionName.text.toString()
            val sessionPassword = binding.editTextJoinSessionPassword.text.toString()

            if (sessionName.isEmpty() || sessionPassword.isEmpty()) {
                if (sessionName.isEmpty()) {
                    binding.editTextJoinSessionName.error = "Session name is required"
                }
                if (sessionPassword.isEmpty()) {
                    binding.editTextJoinSessionPassword.error = "Session password is required"
                }
                return@setOnClickListener
           }
            joinSession(sessionName, sessionPassword)

        }
    }
    private fun joinSession(sessionName: String, sessionPassword: String) {
        val sessionsRef = database.child("sessions")

        sessionsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var sessionFound = false


                for (sessionSnapshot in snapshot.children) {
                    val storedName = sessionSnapshot.child("name").getValue(String::class.java)
                    val storedPassword = sessionSnapshot.child("password").getValue(String::class.java)

                    Log.d("JoinSessionActivity", "Stored Name: $storedName, Stored Password: $storedPassword")

                    if (storedName != null && storedPassword != null && storedName == sessionName) {
                        sessionFound = true

                        if (storedPassword == sessionPassword) {
                            Log.d("JoinSessionActivity", "Password matches, starting session")
                            val intent = Intent(this@JoinSessionActivity, SessionActivity::class.java).apply {
                                putExtra("SESSION_NAME", sessionName)
                                putExtra("SESSION_PASSWORD", sessionPassword)
                                putExtra("USERNAME", username)
                            }
                            startActivity(intent)
                            break
                        } else {
                            binding.editTextJoinSessionPassword.error = "Incorrect password"
                            break
                        }
                    }
                }

                if (!sessionFound) {
                    binding.editTextJoinSessionName.error = "Session does not exist"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("JoinSessionActivity", "Database error: ${error.message}")
            }
        })
    }
}

