package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.SharingResourcesProvider
import com.example.playlistmaker.sharing.domain.models.EmailData

class SharingResourcesProviderImpl (private val context: Context) : SharingResourcesProvider {
    override fun getShareAppLink(): String {
        return context.getString(R.string.share_message)
    }

    override fun getTermsLink(): String {
        return context.getString(R.string.agreement_url)
    }

    override fun getSupportEmailData(): EmailData {
        return EmailData(
            extraSubject = context.getString(R.string.subject),
            extraText = context.getString(R.string.message_to),
            email = context.getString(R.string.my_email),
            sendTitle = context.getString(R.string.share_title),
        )
    }
}