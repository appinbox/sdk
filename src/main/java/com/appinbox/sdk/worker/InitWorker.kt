package com.appinbox.sdk.worker

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.appinbox.sdk.R
import com.appinbox.sdk.model.Device
import com.appinbox.sdk.svc.ApiBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InitWorker(
    context: Context,
    parameters: WorkerParameters
) : Worker(context, parameters) {
    private val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    override fun doWork(): Result {
        val inputData = inputData
        val appId = inputData.getString(WorkerVars.KEY_APP_ID)
        val appKey = inputData.getString(WorkerVars.KEY_APP_KEY)
        val contact = inputData.getString(WorkerVars.KEY_CONTACT)
        val deviceId = inputData.getString(WorkerVars.KEY_DEVICE_ID)
        val progress = "Initializing app inbox"

        // Mark the Worker as important
        setForegroundAsync(createForegroundInfo(progress))
        initialize(appId, appKey, contact, deviceId)
        return Result.success()
    }

    private fun initialize(appId: String?, appKey: String?, contact: String?, deviceId: String?) {
        val device = Device(deviceId!!)
        ApiBuilder.getApi().registerDevice(appId, appKey, contact, device).enqueue(object : Callback<Device?> {
            override fun onResponse(call: Call<Device?>, response: Response<Device?>) {
                notificationManager.cancel(NOTIF_ID)
            }

            override fun onFailure(call: Call<Device?>, t: Throwable) {
                notificationManager.cancel(NOTIF_ID)
            }
        })
    }

    private fun createForegroundInfo(progress: String): ForegroundInfo {
        // Build a notification using bytesRead and contentLength
        val context = applicationContext
        val id = context.getString(R.string.default_notification_channel_id)
        val title = context.getString(R.string.notif_init_title)
        val cancel = context.getString(R.string.notif_cancel)
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(context)
            .createCancelPendingIntent(getId())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
        val notification = NotificationCompat.Builder(context, id)
            .setContentTitle(title)
            .setTicker(title)
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setOngoing(true) // Add the cancel action to the notification which can
            // be used to cancel the worker
            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .build()
        return ForegroundInfo(NOTIF_ID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        // Create a Notification channel
    }

    companion object {
        private const val NOTIF_ID = 12311
    }

}