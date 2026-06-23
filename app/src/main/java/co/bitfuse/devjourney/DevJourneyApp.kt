package co.bitfuse.devjourney

import android.app.Application
import co.bitfuse.devjourney.data.repository.DemoDataSeeder
import co.bitfuse.devjourney.di.ApplicationScope
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
