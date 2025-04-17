package com.example.playlistmaker.sharing.domain.impl

import android.app.Activity
import com.example.playlistmaker.sharing.domain.SharingResourcesProvider
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: (Activity) -> ExternalNavigator,
    private val sharingResourcesProvider: SharingResourcesProvider
) : SharingInteractor {
    override fun shareApp(activity: Activity) {
        externalNavigator(activity).shareLink(sharingResourcesProvider.getShareAppLink())
    }

    override fun openTerms(activity: Activity) {
        externalNavigator(activity).openLink(sharingResourcesProvider.getTermsLink())
    }

    override fun openSupport(activity: Activity) {
        externalNavigator(activity).openEmail(sharingResourcesProvider.getSupportEmailData())
    }

}