package com.example.playlistmaker.sharing.data.impl

import android.app.Activity
import android.content.Intent
import androidx.core.net.toUri
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.models.EmailData

class ExternalNavigatorImpl(private val activity: Activity) : ExternalNavigator {

    override fun shareLink(link: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, link)

        }
        activity.startActivity(Intent.createChooser(intent, "Поделиться через"))
    }

    override fun openLink(link: String) {
        val intent = Intent(Intent.ACTION_VIEW, link.toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        activity.startActivity(intent)
    }

    override fun openEmail(emailData: EmailData) {

        val intent = Intent(Intent.ACTION_SENDTO, "mailto:".toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.email))
            putExtra(Intent.EXTRA_SUBJECT, emailData.extraSubject)
            putExtra(Intent.EXTRA_TEXT, emailData.extraText)
        }
        activity.startActivity(Intent.createChooser(intent, emailData.sendTitle))

    }
}