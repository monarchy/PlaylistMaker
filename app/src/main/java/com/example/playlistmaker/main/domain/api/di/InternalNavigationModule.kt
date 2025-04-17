package com.example.playlistmaker.main.domain.api.di

import android.app.Activity
import com.example.playlistmaker.main.data.impl.InternalNavigationImpl
import com.example.playlistmaker.main.domain.impl.InternalNavigation
import org.koin.dsl.module

val internalNavigationModule = module {

    factory<InternalNavigation> { (activity: Activity) ->
        InternalNavigationImpl(activity)
    }
}