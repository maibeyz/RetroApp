package com.mai.retroapp.ui.view.ItemTouchHelper

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mai.retroapp.ui.view.CardAdapter

class CardMoveCallback(
    private val onItemMoved: (fromAdapter: CardAdapter, toAdapter: CardAdapter, fromPosition: Int, toPosition: Int) -> Unit
) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {

        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        val fromAdapter = viewHolder.bindingAdapter as CardAdapter
        val toAdapter = target.bindingAdapter as CardAdapter


        val card = fromAdapter.getCardAt(fromPosition)

        fromAdapter.removeCard(fromPosition)
        fromAdapter.notifyItemRemoved(fromPosition)

        toAdapter.addCardAt(toPosition, card)
        toAdapter.notifyItemInserted(toPosition)

        onItemMoved(fromAdapter, toAdapter, fromPosition, toPosition)
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder?.itemView?.visibility = View.VISIBLE
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val alpha = 1.0f - Math.abs(dX) / recyclerView.width
            viewHolder.itemView.alpha = alpha
            viewHolder.itemView.translationX = dX
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }
}