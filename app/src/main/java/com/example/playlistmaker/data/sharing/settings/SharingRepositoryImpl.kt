package com.example.playlistmaker.data.sharing.settings

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.shairing.settings.SharingRepository

class SharingRepositoryImpl(private val contextActivity: Context, private val context: Context) : SharingRepository {
    override fun openSupport() {
        val writeToSupportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.support_email)))
            putExtra(
                Intent.EXTRA_SUBJECT,
                context.getString(R.string.thanks_to_devs)
            )
            putExtra(
                Intent.EXTRA_TEXT,
                context.getString(R.string.thanks_to_devs)
            )
        }
        contextActivity.startActivity(writeToSupportIntent)
    }

    override fun openTerms() {
        val userAgreementIntent =
            Intent(Intent.ACTION_VIEW, context.getString(R.string.yandex_ru_legal_practicum_offer).toUri())
        contextActivity.startActivity(userAgreementIntent)
    }

    override fun shareApp() {
        val shareApp = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.yandex_ru_android_developer))
            type = "text/plain"
        }
        contextActivity.startActivity(shareApp)
    }
}
