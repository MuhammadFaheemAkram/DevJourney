package co.bitfuse.devjourney.core.notification

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LearningReminderScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val workManager: WorkManager
        get() = WorkManager.getInstance(context)

    fun setDailyReminderEnabled(enabled: Boolean) {
        if (enabled) {
            scheduleDailyReminder()
        } else {
            workManager.cancelUniqueWork(DAILY_REMINDER_WORK_NAME)
        }
    }

    private fun scheduleDailyReminder() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()
        val request = PeriodicWorkRequestBuilder<LearningReminderWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            DAILY_REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            request,
        )
    }

    private companion object {
        const val DAILY_REMINDER_WORK_NAME = "devjourney_daily_learning_reminder"
    }
}
