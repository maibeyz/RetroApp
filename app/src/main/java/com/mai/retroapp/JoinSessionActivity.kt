package com.mai.retroapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mai.retroapp.databinding.ActivityJoinSessionBinding

class JoinSessionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinSessionBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_join_session)

        binding = ActivityJoinSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference

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

            val sessionRef = database.child("sessions").child(sessionName)
            sessionRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val storedPassword = snapshot.child("password").getValue(String::class.java)
                        if (storedPassword == sessionPassword) {
                            val intent = Intent(this@JoinSessionActivity, MainActivity::class.java).apply {
                                putExtra("SESSION_NAME", sessionName)
                                putExtra("SESSION_PASSWORD", sessionPassword)
                            }
                            startActivity(intent)
                        } else {
                            binding.editTextJoinSessionPassword.error = "Incorrect password"
                        }
                    } else {
                        binding.editTextJoinSessionName.error = "Session does not exist"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            view.setPadding(0, 0, 0, imeInsets.bottom)
            insets
        }
    }
}

