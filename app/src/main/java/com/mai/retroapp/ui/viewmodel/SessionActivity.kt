package com.mai.retroapp.ui.viewmodel

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mai.retroapp.R
import com.mai.retroapp.data.model.Card
import com.mai.retroapp.data.repository.SessionRepository
import com.mai.retroapp.databinding.ActivitySessionBinding
import com.mai.retroapp.ui.util.GlobalCountDownTimer
import com.mai.retroapp.ui.view.CardAdapter
import com.mai.retroapp.ui.view.ItemTouchHelper.CardMoveCallback


class SessionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionBinding
    private lateinit var cardAdapterWentWell: CardAdapter
    private lateinit var cardAdapterToImprove: CardAdapter
    private lateinit var cardAdapterActionItems: CardAdapter
    private lateinit var sessionName: String
    private lateinit var sessionRepository: SessionRepository
    private lateinit var username: String
    private var isAnonymous: Boolean = false
    private var sessionDuration: Long = 0
    private var isTimerRunning: Boolean = false
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var textViewTimer: TextView
    private var remainingTime: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_session)

        isAnonymous = intent.getBooleanExtra("IS_ANONYMOUS", false)
        val showUsername = intent.getBooleanExtra("SHOW_USERNAME", false)
        username = intent.getStringExtra("USERNAME") ?: "Anonymous"
        sessionDuration = intent.getLongExtra("SESSION_DURATION", 0) * 60 * 1000

        binding = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textViewTimer = binding.textViewTimer

        sessionName = intent.getStringExtra("SESSION_NAME") ?: "Default Session"
        binding.textViewSessionTitle.text = sessionName

        sessionRepository = SessionRepository()

        setupRecyclerViews()
        loadCards(showUsername)

        binding.buttonAddCard.setOnClickListener {
            val content = binding.editTextCardContent.text.toString()
            showPopupMenu(it, content, username)
        }

        remainingTime = sessionDuration
        startTimer(sessionDuration)
    }

    private fun startTimer(duration: Long) {
        isTimerRunning = true
        GlobalCountDownTimer.startTimer(duration)
        GlobalCountDownTimer.onTickListener = { millisUntilFinished ->
            val secondsRemaining = millisUntilFinished / 1000
            textViewTimer.text = "Kalan Süre: $secondsRemaining saniye"
        }
        GlobalCountDownTimer.onFinishListener = {
            isTimerRunning = false
            textViewTimer.text = "Kalan Süre: 0 saniye"
            Toast.makeText(
                this@SessionActivity,
                "Süre doldu, artık kart ekleyemezsiniz.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun setupRecyclerViews() {
        cardAdapterWentWell = CardAdapter()
        cardAdapterToImprove = CardAdapter()
        cardAdapterActionItems = CardAdapter()

        val itemTouchHelperCallback =
            CardMoveCallback { fromAdapter, toAdapter, fromPosition, toPosition ->
                if (fromAdapter != toAdapter) {
                    val movedCard = fromAdapter.removeCard(fromPosition)
                    toAdapter.addCardAt(toPosition, movedCard)
                    fromAdapter.notifyItemRemoved(fromPosition)
                    toAdapter.notifyItemInserted(toPosition)
                } else {
                    fromAdapter.onItemMove(fromPosition, toPosition)
                    fromAdapter.notifyItemMoved(fromPosition, toPosition)
                }
            }

        setupRecyclerView(
            binding.recyclerWentWell,
            cardAdapterWentWell,
            itemTouchHelperCallback,
            LinearLayoutManager.HORIZONTAL
        )
        setupRecyclerView(
            binding.recyclerToImprove,
            cardAdapterToImprove,
            itemTouchHelperCallback,
            LinearLayoutManager.HORIZONTAL
        )
        setupRecyclerView(
            binding.recyclerActionItems,
            cardAdapterActionItems,
            itemTouchHelperCallback,
            LinearLayoutManager.HORIZONTAL
        )
    }

    private fun setupRecyclerView(
        recyclerView: RecyclerView,
        adapter: CardAdapter,
        callback: CardMoveCallback,
        orientation: Int
    ) {
        recyclerView.layoutManager =
            LinearLayoutManager(this, orientation, false)
        recyclerView.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun loadCards(showUsername: Boolean) {
        sessionRepository.getSessionCards(sessionName).observe(this@SessionActivity) { cards ->

            val wentWellCards = cards.filter { it.type == "Went Well" }
            val toImproveCards = cards.filter { it.type == "To Improve" }
            val actionItemsCards = cards.filter { it.type == "Action Items" }

            setUsernameForCards(wentWellCards, showUsername)
            setUsernameForCards(toImproveCards, showUsername)
            setUsernameForCards(actionItemsCards, showUsername)


            cardAdapterWentWell.submitList(wentWellCards)
            cardAdapterToImprove.submitList(toImproveCards)
            cardAdapterActionItems.submitList(actionItemsCards)
        }
    }

    private fun setUsernameForCards(cards: List<Card>, showUsername: Boolean) {
        val finalUsername = when {
            isAnonymous -> "Anonymous"
            showUsername -> username
            else -> ""
        }
        cards.forEach { it.username = finalUsername }
    }

    private fun showPopupMenu(view: View, content: String, username: String) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.recycler_view_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->

            when (menuItem.itemId) {
                R.id.recyclerWentWell -> addCard(content, "Went Well", username)
                R.id.recyclerToImprove -> addCard(content, "To Improve", username)
                R.id.recyclerActionItems -> addCard(content, "Action Items", username)
            }
            true
        }

        popupMenu.show()
    }

    private fun addCard(content: String, type: String, username: String) {
        if (!isTimerRunning) {
            Toast.makeText(this, "Süre dolmuş, kart ekleyemezsiniz.", Toast.LENGTH_SHORT).show()
            return
        }
        if (content.isNotEmpty()) {
                val cardId =
                    sessionRepository.database.child("sessions").child(sessionName).child("cards")
                        .push().key
                if (cardId != null) {
                    val backgroundColor = when (type) {
                        "Went Well" -> "#F0EBE5"
                        "To Improve" -> "#D1DFBB"
                        "Action Items" -> "#6087575C"
                        else -> "#FFFFFF"
                    }

                    val card = Card(
                        id = cardId,
                        content = content,
                        type = type,
                        backgroundColor = backgroundColor,
                        username = username
                    )

                    sessionRepository.addCardToSession(sessionName, card)

                    when (type) {
                        "Went Well" -> cardAdapterWentWell.addCard(card)
                        "To Improve" -> cardAdapterToImprove.addCard(card)
                        "Action Items" -> cardAdapterActionItems.addCard(card)
                    }

                    Toast.makeText(this, "Card added successfully!", Toast.LENGTH_SHORT).show()
                    binding.editTextCardContent.text.clear()
                } else {
                    Toast.makeText(this, "Error generating card ID.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter card content.", Toast.LENGTH_SHORT).show()
            }
        }
    override fun onDestroy() {
        super.onDestroy()
        if (this::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
    }
}



