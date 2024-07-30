package com.mai.retroapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.mai.retroapp.R
import com.mai.retroapp.data.model.Session
import com.mai.retroapp.data.repository.SessionRepository
import com.mai.retroapp.ui.viewmodel.SessionViewModel
import com.mai.retroapp.ui.viewmodel.SessionViewModelFactory

class SessionActivity : AppCompatActivity() {

    private lateinit var sessionViewModel: SessionViewModel

    lateinit var buttonSaveSession: Button
    lateinit var editTextSessionName: EditText
    lateinit var editTextSessionPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_session)

        buttonSaveSession = findViewById(R.id.buttonSaveSession)
        editTextSessionName = findViewById(R.id.editTextSessionName)
        editTextSessionPassword = findViewById(R.id.editTextSessionPassword)

        val repository = SessionRepository()
        val viewModelFactory = SessionViewModelFactory(repository)
        sessionViewModel = ViewModelProvider(this, viewModelFactory).get(SessionViewModel::class.java)


        buttonSaveSession.setOnClickListener {
            val sessionName = editTextSessionName.text.toString()
            val sessionPassword = editTextSessionPassword.text.toString()

            val session = Session(name = sessionName, password = sessionPassword)
            sessionViewModel.addSession(session)

            finish()
        }
    }
}