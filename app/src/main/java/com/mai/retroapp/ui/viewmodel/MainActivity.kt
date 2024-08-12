package com.mai.retroapp.ui.viewmodel

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mai.retroapp.R
import com.mai.retroapp.data.model.Session
import com.mai.retroapp.databinding.ActivityMainBinding
import com.mai.retroapp.ui.view.SessionAdapter


class MainActivity : AppCompatActivity() {



    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference
    private lateinit var sessionAdapter: SessionAdapter
    private val sessionList = mutableListOf<Session>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference
        sessionAdapter = SessionAdapter(sessionList)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = sessionAdapter
        }

        loadSessions()


        binding.buttonCreateSession.setOnClickListener {
            val intent = Intent(this, SessionSettingsActivity::class.java)
            startActivity(intent)
        }

        binding.buttonJoinSession.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val database = FirebaseDatabase.getInstance().reference.child("sessions")

    }

    private fun loadSessions() {
        database.child("sessions").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sessionList.clear()
                for (sessionSnapshot in snapshot.children) {
                    val session = sessionSnapshot.getValue(Session::class.java)
                    if (session != null) {
                        sessionList.add(session) // Listeye ekle
                    }
                }
                sessionAdapter.notifyDataSetChanged() // Adapterı güncelle
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Hata: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

