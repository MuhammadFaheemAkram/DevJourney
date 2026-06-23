package co.bitfuse.devjourney.core.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class LearningReminderWorker(
    appContext: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(appContext, workerParameters) {
    override suspend fun doWork(): Result {
        // Phase 6 schedules the offline reminder pipeline. A later notification phase can attach
        // runtime permission handling and user-visible notifications without changing callers.
        return Result.success()
    }
}
