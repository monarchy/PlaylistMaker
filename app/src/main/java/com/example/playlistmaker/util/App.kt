package com.example.playlistmaker.util

import android.app.Application
import com.example.playlistmaker.main.domain.api.di.internalNavigationModule
import com.example.playlistmaker.main.ui.di.mainViewModelModule
import com.example.playlistmaker.player.ui.activity.di.playerViewModelModule
import com.example.playlistmaker.search.data.di.searchDataModule
import com.example.playlistmaker.search.domain.di.interactorModule
import com.example.playlistmaker.search.domain.di.trackRepositoryModule
import com.example.playlistmaker.search.ui.di.searchViewModelModule
import com.example.playlistmaker.settings.data.di.settingsRepositoryModule
import com.example.playlistmaker.settings.domain.di.settingsInteractorModule
import com.example.playlistmaker.settings.ui.di.settingsViewModelModule
import com.example.playlistmaker.sharing.data.di.sharingResourcesProviderModule
import com.example.playlistmaker.sharing.domain.di.externalNavigatorModule
import com.example.playlistmaker.sharing.domain.di.sharingInteractorModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {


    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                searchDataModule,
                searchViewModelModule,
                trackRepositoryModule,
                interactorModule,
                internalNavigationModule,
                mainViewModelModule,
                playerViewModelModule,
                externalNavigatorModule,
                sharingResourcesProviderModule,
                sharingInteractorModule,
                settingsRepositoryModule,
                settingsInteractorModule,
                settingsViewModelModule
            )
        }
    }
}