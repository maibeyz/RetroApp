package com.mai.retroapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mai.retroapp.data.model.Session
import com.mai.retroapp.data.repository.SessionRepository

class SessionViewModel(private val repository: SessionRepository) : ViewModel() {

    private val _sessions = MutableLiveData<List<Session>>()
    val sessions: LiveData<List<Session>> get() = _sessions

    fun addSession(session: Session) {
        repository.addSession(session)
    }

    fun updateSession(session: Session) {
        repository.updateSession(session)
    }

    fun setSessions(sessions: List<Session>) {
        _sessions.value = sessions
    }

}

class SessionViewModelFactory(private val repository: SessionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SessionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SessionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}