package com.mai.retroapp.ui.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mai.retroapp.R
import com.mai.retroapp.data.model.Card

class CardAdapter : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    private val cards = mutableListOf<Card>()

    fun submitList(cardList: List<Card>) {
        cards.clear()
        cards.addAll(cardList)
        notifyDataSetChanged()
    }
    fun addCard(card: Card) {
        cards.add(card)
        notifyItemInserted(cards.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cards[position])
    }

    override fun getItemCount(): Int = cards.size

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contentTextView: TextView = itemView.findViewById(R.id.textViewCardContent)

        fun bind(card: Card) {
            contentTextView.text = card.content
        }

    }
}