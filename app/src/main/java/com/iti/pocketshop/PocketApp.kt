package com.iti.pocketshop

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.initialize
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PocketApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Firebase.initialize(context = this)
        AppCheckInstaller.install()
    }
}
