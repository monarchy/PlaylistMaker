package com.example.playlistmaker.domain.sharing.impl

import com.example.playlistmaker.data.sharing.SharingRepository
import com.example.playlistmaker.domain.sharing.SharingInteractor

class SharingInteractorImpl(private val sharingRepository: SharingRepository):SharingInteractor {
    override fun openSupport() {
        sharingRepository.openSupport()
    }

    override fun openTerms() {
        sharingRepository.openTerms()
    }

    override fun shareApp() {
        sharingRepository.shareApp()
    }
}