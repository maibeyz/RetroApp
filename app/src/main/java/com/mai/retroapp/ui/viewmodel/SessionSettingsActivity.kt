package com.mai.retroapp.ui.viewmodel

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mai.retroapp.R
import com.mai.retroapp.data.model.Session
import com.mai.retroapp.databinding.ActivitySessionSettingsBinding
import com.mai.retroapp.ui.viewmodel.SessionActivity

class SessionSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionSettingsBinding
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_session_settings)

        binding = ActivitySessionSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance().reference

        binding.buttonStartSession.setOnClickListener {
            val sessionName = binding.editTextSessionName.text.toString()
            val sessionPassword = binding.editTextPassword.text.toString()
            if (sessionName.isNotEmpty() && sessionPassword.isNotEmpty()) {
                createSession(sessionName, sessionPassword)
            } else {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createSession(sessionName: String, sessionPassword: String) {
        val session = Session(name = sessionName, password = sessionPassword)
        val sessionId = database.child("sessions").push().key ?: return

        database.child("sessions").child(sessionId).setValue(session)
            .addOnSuccessListener {
                val intent = Intent(this, SessionActivity::class.java).apply {
                    putExtra("SESSION_NAME", sessionName)
                }
                startActivity(intent)
            }.addOnFailureListener {
                Toast.makeText(this, "Oturum oluşturulurken hata oluştu", Toast.LENGTH_SHORT).show()
            }
    } }

