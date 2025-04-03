package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.domain.SharingResourcesProvider
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val sharingResourcesProvider: SharingResourcesProvider
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(sharingResourcesProvider.getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(sharingResourcesProvider.getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(sharingResourcesProvider.getSupportEmailData())
    }

}