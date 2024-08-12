package com.mai.retroapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mai.retroapp.data.model.Card
import com.mai.retroapp.data.model.Session

class SessionRepository {

    val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("sessions")

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
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("SessionRepository", "Session added successfully: $session")
                } else {
                    Log.e("SessionRepository", "Error adding session", task.exception)
                }
            }
    }


    fun addCardToSession(sessionName: String, card: Card) {
        database.child("sessions").child(sessionName).child("cards").child(card.id).setValue(card)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                } else {

                }
            }
    }


    fun getSessionCards(sessionName: String): LiveData<List<Card>> {
        val cardsLiveData = MutableLiveData<List<Card>>()

        database.child("sessions").child(sessionName).child("cards")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val cards = mutableListOf<Card>()
                    for (cardSnapshot in snapshot.children) {
                        val card = cardSnapshot.getValue(Card::class.java)
                        card?.let { cards.add(it) }
                    }
                    cardsLiveData.postValue(cards)
                }

                override fun onCancelled(error: DatabaseError) {

                    Log.e("SessionRepository", "Error fetching cards: ${error.message}")
                }
            })

        return cardsLiveData
    }
}
