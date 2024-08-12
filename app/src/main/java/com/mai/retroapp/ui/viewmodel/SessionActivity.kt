package com.mai.retroapp.ui.viewmodel

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mai.retroapp.R
import com.mai.retroapp.data.model.Card
import com.mai.retroapp.data.repository.SessionRepository
import com.mai.retroapp.databinding.ActivitySessionBinding
import com.mai.retroapp.ui.view.CardAdapter


class SessionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionBinding
    private lateinit var cardAdapterWentWell: CardAdapter
    private lateinit var cardAdapterToImprove: CardAdapter
    private lateinit var cardAdapterActionItems: CardAdapter
    private lateinit var sessionName: String
    private lateinit var sessionRepository: SessionRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_session)

        binding = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionName = intent.getStringExtra("SESSION_NAME") ?: "Default Session"
        binding.textViewSessionTitle.text = sessionName

        sessionRepository = SessionRepository()

        setupRecyclerViews()
        loadCards()

        binding.buttonAddCard.setOnClickListener {
            showPopupMenu(it)
        }
    }


    private fun setupRecyclerViews() {
        cardAdapterWentWell = CardAdapter()
        cardAdapterToImprove = CardAdapter()
        cardAdapterActionItems = CardAdapter()

        binding.recyclerWentWell.apply {
            layoutManager = LinearLayoutManager(this@SessionActivity)
            adapter = cardAdapterWentWell
        }

        binding.recyclerToImprove.apply {
            layoutManager = LinearLayoutManager(this@SessionActivity)
            adapter = cardAdapterToImprove
        }

        binding.recyclerActionItems.apply {
            layoutManager = LinearLayoutManager(this@SessionActivity)
            adapter = cardAdapterActionItems
        }
    }


    private fun loadCards() {
        sessionRepository.getSessionCards(sessionName).observe(this@SessionActivity) { cards ->
            val wentWellCards = cards.filter { it.type == "Went Well" }
            val toImproveCards = cards.filter { it.type == "To Improve" }
            val actionItemsCards = cards.filter { it.type == "Action Items" }

            cardAdapterWentWell.submitList(wentWellCards)
            cardAdapterToImprove.submitList(toImproveCards)
            cardAdapterActionItems.submitList(actionItemsCards)
        }
    }
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.recycler_view_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            val content = binding.editTextCardContent.text.toString()

            when (menuItem.itemId) {
                R.id.recyclerWentWell -> addCard(content, "Went Well")
                R.id.recyclerToImprove -> addCard(content, "To Improve")
                R.id.recyclerActionItems -> addCard(content, "Action Items")
            }
            true
        }

        popupMenu.show()
    }

    private fun addCard(content: String, type: String) {
        if (content.isNotEmpty()) {
            val cardId = sessionRepository.database.child("sessions").child(sessionName).child("cards").push().key
            if (cardId != null) {
                val card = Card(id = cardId, content = content, type = type)
                sessionRepository.addCardToSession(sessionName, card)

                Toast.makeText(this, "Card added successfully!", Toast.LENGTH_SHORT).show()
                binding.editTextCardContent.text.clear()


                when (type) {
                    "Went Well" -> {
                        cardAdapterWentWell.addCard(card)
                        cardAdapterWentWell.notifyItemInserted(cardAdapterWentWell.itemCount - 1)
                    }
                    "To Improve" -> {
                        cardAdapterToImprove.addCard(card)
                        cardAdapterToImprove.notifyItemInserted(cardAdapterToImprove.itemCount - 1)
                    }
                    "Action Items" -> {
                        cardAdapterActionItems.addCard(card)
                        cardAdapterActionItems.notifyItemInserted(cardAdapterActionItems.itemCount - 1)
                    }
                }
            } else {
                Toast.makeText(this, "Error generating card ID.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please enter card content.", Toast.LENGTH_SHORT).show()
        }
    }
}
