package com.example.playlistmaker.media.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.playlistmaker.R
import com.example.playlistmaker.main.ui.activity.MainActivity
import com.example.playlistmaker.media.ui.ViewPagerAdapter
import com.example.playlistmaker.media.ui.fragments.FavoriteTracksFragment
import com.example.playlistmaker.media.ui.fragments.PlaylistsFragment
import com.example.playlistmaker.media.ui.viewmodel.MediatekaViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediatekaActivity : AppCompatActivity() {
    private val viewModel: MediatekaViewModel by viewModel()
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mediateka)

        findViewById<View>(R.id.goBackButton).setOnClickListener {
            navigateBack()
        }

        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)

        val fragments = listOf(PlaylistsFragment(), FavoriteTracksFragment())
        val adapter = ViewPagerAdapter(this, fragments)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.favourite_tracks)
                1 -> getString(R.string.playlists)
                else -> null
            }
        }.attach()

        val lastTab = savedInstanceState?.getInt("selected_tab", 0) ?: 0
        viewPager.setCurrentItem(lastTab, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("selected_tab", viewPager.currentItem)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateBack()
    }

    private fun navigateBack() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }
}