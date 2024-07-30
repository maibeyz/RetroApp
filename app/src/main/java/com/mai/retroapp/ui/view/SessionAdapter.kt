package com.mai.retroapp.ui.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mai.retroapp.data.model.Session
import com.mai.retroapp.databinding.ItemSessionBinding
import java.util.Collections

class SessionAdapter : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

    private var sessions = mutableListOf<Session>()

    fun submitList(sessionList: List<Session>) {
        sessions = sessionList.toMutableList()
        notifyDataSetChanged()
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(sessions, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val binding = ItemSessionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SessionViewHolder(binding)
    }

    override fun getItemCount(): Int = sessions.size

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        holder.bind(sessions[position])
    }

    class SessionViewHolder(private val binding: ItemSessionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(session: Session) {
            binding.session = session
            binding.executePendingBindings()
        }
    }
}

class SessionDiffCallback : DiffUtil.ItemCallback<Session>() {
    override fun areItemsTheSame(oldItem: Session, newItem: Session): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Session, newItem: Session): Boolean {
        return oldItem == newItem
    }
}