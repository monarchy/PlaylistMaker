package com.example.playlistmaker.settings.ui.activity

import android.app.Application
import com.example.playlistmaker.util.Creator

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Creator.initialize(this)
    }
}