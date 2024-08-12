package com.mai.retroapp.ui.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mai.retroapp.R
import com.mai.retroapp.data.model.Session

class SessionAdapter(private val sessions: List<Session>) : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

    class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sessionName: TextView = itemView.findViewById(R.id.textViewSessionName)

        fun bind(session: Session) {
            sessionName.text = session.name // Session ismini göster
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_session, parent, false)
        return SessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        holder.bind(sessions[position])
    }

    override fun getItemCount(): Int {
        return sessions.size
    }
}