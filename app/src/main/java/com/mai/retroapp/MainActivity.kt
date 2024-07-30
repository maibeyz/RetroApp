package com.mai.retroapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mai.retroapp.data.model.Session
import com.mai.retroapp.data.repository.SessionRepository
import com.mai.retroapp.databinding.ActivityMainBinding
import com.mai.retroapp.ui.view.ItemTouchHelperCallback
import com.mai.retroapp.ui.view.SessionAdapter
import com.mai.retroapp.ui.viewmodel.SessionViewModel
import com.mai.retroapp.ui.viewmodel.SessionViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var buttonAddSession: Button
    private lateinit var sessionViewModel: SessionViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var database: DatabaseReference
    private lateinit var username: String
    private var timer: Int = 3
    private var isAnonymous: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        buttonAddSession = findViewById(R.id.buttonAddSession)
        recyclerView = findViewById(R.id.recyclerView)

        //get username
        username = intent.getStringExtra("USERNAME") ?: "Anonymous"
        timer = intent.getIntExtra("TIMER", 3)
        isAnonymous = intent.getBooleanExtra("IS_ANONYMOUS", false)

        //database
        val database = FirebaseDatabase.getInstance().reference.child("sessions")

        //viewmodel
        val repository = SessionRepository()
        val viewModelFactory = SessionViewModelFactory(repository)
        sessionViewModel = ViewModelProvider(this, viewModelFactory).get(SessionViewModel::class.java)

        //recyclerview
        recyclerView.layoutManager = LinearLayoutManager(this)
        val sessionAdapter = SessionAdapter()
        recyclerView.adapter = sessionAdapter

        //drag and drop
        val itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(sessionAdapter))
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        //some changes

        //livedata
        sessionViewModel.sessions.observe(this) { sessions ->
            sessionAdapter.submitList(sessions)
        }

        //add session
        buttonAddSession.setOnClickListener {
            val intent = Intent(this, SessionActivity::class.java)
            startActivity(intent)
        }

        //database listener
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val sessions = mutableListOf<Session>()
                for (sessionSnapshot in snapshot.children) {
                    val session = sessionSnapshot.getValue(Session::class.java)
                    if (session != null) {
                        sessions.add(session)
                    }
                }
                sessionViewModel.setSessions(sessions)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        //crashlytics
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
    }

    }