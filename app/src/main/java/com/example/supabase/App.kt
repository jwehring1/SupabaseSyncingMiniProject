package com.example.supabase

import android.app.Application
import com.example.supabase.domain.usecase.Sync
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Sync.initialize(context = this)
    }
}
