package com.mai.retroapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mai.retroapp.data.model.Session

class SessionRepository {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("sessions")

    private val _sessions = MutableLiveData<List<Session>>()
    val sessions: LiveData<List<Session>> get() = _sessions

    init {

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val sessionList = mutableListOf<Session>()
                for (sessionSnapshot in snapshot.children) {
                    val session = sessionSnapshot.getValue(Session::class.java)
                    if (session != null) {
                        sessionList.add(session)
                    }
                }
                _sessions.value = sessionList
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    fun addSession(session: Session) {
        val sessionId = database.push().key ?: return
        session.id = sessionId
        database.child(sessionId).setValue(session)
    }


    fun updateSession(session: Session) {
        database.child(session.id).setValue(session)
    }
}