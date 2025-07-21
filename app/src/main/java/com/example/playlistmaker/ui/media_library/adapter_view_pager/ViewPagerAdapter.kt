package com.example.playlistmaker.ui.media_library.adapter_view_pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.ui.media_library.favorite.FavoriteTracksFragment
import com.example.playlistmaker.ui.media_library.library.LibraryFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FavoriteTracksFragment.newInstance()
            else -> LibraryFragment.newInstance()
        }
    }
}