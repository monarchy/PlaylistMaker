package com.example.playlistmaker.main.data.impl

import android.app.Activity
import android.content.Intent
import com.example.playlistmaker.main.domain.impl.InternalNavigation
import com.example.playlistmaker.media.ui.activity.MediatekaActivity
import com.example.playlistmaker.search.ui.activity.SearchActivity
import com.example.playlistmaker.settings.ui.activity.SettingsActivity

class InternalNavigationImpl(private val activity: Activity): InternalNavigation {
    override fun startSearch() {
        val intent = Intent(activity, SearchActivity::class.java)
        activity.startActivity(intent)
    }

    override fun startMedia() {
        val intent = Intent(activity, MediatekaActivity::class.java)
        activity.startActivity(intent)
    }

    override fun startSettings() {
        val intent = Intent(activity, SettingsActivity::class.java)
        activity.startActivity(intent)
    }
}