package com.example.playlistmaker.sharing.domain.di

import android.app.Activity
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import org.koin.dsl.module

val externalNavigatorModule = module {
    factory<ExternalNavigator> { (activity: Activity) ->
        ExternalNavigatorImpl(activity)
    }
}