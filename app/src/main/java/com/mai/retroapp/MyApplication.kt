package com.mai.retroapp

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
    }
}