package com.example.devjourney

import android.app.Application
import com.example.devjourney.data.repository.DemoDataSeeder
import com.example.devjourney.di.ApplicationScope
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@HiltAndroidApp
class DevJourneyApp : Application() {
    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    @Inject
    lateinit var demoDataSeeder: DemoDataSeeder

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            demoDataSeeder.seedIfEmpty()
        }
    }
}
