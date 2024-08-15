package com.mai.retroapp.ui.view

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
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

    fun addCardAt(position: Int, card: Card) {
        cards.add(position, card)
        notifyItemInserted(position)
    }

    fun removeCard(position: Int): Card {
        val card = cards.removeAt(position)
        notifyItemRemoved(position)
        return card
    }

    fun getCardAt(position: Int): Card {
        return cards[position]
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        val card = cards.removeAt(fromPosition)
        cards.add(toPosition, card)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        holder.bind(cards[position])
        holder.usernameTextView.text = card.username

    }

    override fun getItemCount(): Int = cards.size

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contentTextView: TextView = itemView.findViewById(R.id.textViewCardContent)
        val usernameTextView: TextView = itemView.findViewById(R.id.textViewCardUsername)
        private val cardView: CardView = itemView.findViewById(R.id.cardView)

        fun bind(card: Card) {
            contentTextView.text = card.content
            cardView.setCardBackgroundColor(Color.parseColor(card.backgroundColor))
            usernameTextView.text = card.username.takeIf { it.isNotEmpty() } ?: "Anonymous"

        }
    }
}