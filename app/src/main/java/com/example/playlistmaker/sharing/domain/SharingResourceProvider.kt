package com.example.playlistmaker.sharing.domain

import com.example.playlistmaker.sharing.domain.models.EmailData

interface SharingResourcesProvider {

    fun getShareAppLink(): String
    fun getTermsLink(): String
    fun getSupportEmailData(): EmailData

}