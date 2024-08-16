package com.mai.retroapp.ui.util

import android.os.CountDownTimer

object GlobalCountDownTimer {
    private var timer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0
    private var isRunning: Boolean = false

    // Listener for updates
    var onTickListener: ((millisUntilFinished: Long) -> Unit)? = null
    var onFinishListener: (() -> Unit)? = null

    fun startTimer(durationInMillis: Long) {
        if (isRunning) return  // Prevent starting if it's already running

        timeLeftInMillis = durationInMillis

        timer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                onTickListener?.invoke(millisUntilFinished)
            }

            override fun onFinish() {
                isRunning = false
                onFinishListener?.invoke()
            }
        }.start()

        isRunning = true
    }

    fun pauseTimer() {
        timer?.cancel()
        isRunning = false
    }

    fun resumeTimer() {
        startTimer(timeLeftInMillis)
    }

    fun cancelTimer() {
        timer?.cancel()
        timeLeftInMillis = 0
        isRunning = false
    }

    fun getTimeLeft(): Long {
        return timeLeftInMillis
    }

    fun isTimerRunning(): Boolean {
        return isRunning
    }
}