package com.example.playlistmaker.data.sharing.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.domain.sharing.SharingRepository

class SharingRepositoryImpl(private val contextActivity: Context) : SharingRepository {
    override fun openSupport() {
        val writeToSupportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("gorpishin97@yandex.ru"))
            putExtra(
                Intent.EXTRA_SUBJECT,
                "Сообщение разработчикам и разработчицам приложения Playlist Maker"
            )
            putExtra(
                Intent.EXTRA_TEXT,
                "Спасибо разработчикам и разработчицам за крутое приложение!"
            )
        }
        contextActivity.startActivity(writeToSupportIntent)
    }

    override fun openTerms() {
        val userAgreementIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse("https://yandex.ru/legal/practicum_offer/"))
        contextActivity.startActivity(userAgreementIntent)
    }

    override fun shareApp(link: String) {
        val shareApp = Intent(Intent.ACTION_SEND).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, link)
        }
        contextActivity.startActivity(shareApp)
    }
}