package com.appinbox.sdk

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.appinbox.sdk.ui.AppInboxActivity
import com.appinbox.sdk.worker.ClearWorker
import com.appinbox.sdk.worker.InitWorker
import com.appinbox.sdk.worker.WorkerVars
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AppInboxSDK {
    companion object {
        private const val TAG = "SDK"
        const val SDK_VERSION = "1.5.18"
        @JvmStatic
        fun init(activity: Activity, appId: String, appKey: String, contact: String) {
            savePrefs(activity, appId, appKey, contact)
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task: Task<String?> ->
                    val TAG = "SDK:FBSVC"
                    if (!task.isSuccessful) {
                        Log.w(
                            TAG,
                            "Fetching FCM registration token failed",
                            task.exception
                        )
                        return@addOnCompleteListener
                    }
                    saveDeviceId(activity, task.result)
                    val data = Data.Builder()
                        .putString(WorkerVars.KEY_APP_ID, appId)
                        .putString(WorkerVars.KEY_APP_KEY, appKey)
                        .putString(WorkerVars.KEY_CONTACT, contact)
                        .putString(WorkerVars.KEY_DEVICE_ID, task.result)
                        .build()
                    val request: WorkRequest =
                        OneTimeWorkRequest.Builder(InitWorker::class.java).setInputData(data)
                            .build()
                    WorkManager.getInstance(activity).enqueue(request)
                }
        }

        @JvmStatic
        fun open(activity: Activity, appId: String, appKey: String, contact: String) {
            savePrefs(activity, appId, appKey, contact)
            //        Intent i = new Intent(activity, ListActivity.class);
            val i = Intent(activity, AppInboxActivity::class.java)
            activity.startActivity(i)
        }

        @JvmStatic
        fun logout(activity: Activity) {
            val preferences = activity.getSharedPreferences(
                activity.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
            val appId = preferences.getString(activity.getString(R.string.sp_app), "")
            val appKey = preferences.getString(activity.getString(R.string.sp_key), "")
            val contact = preferences.getString(activity.getString(R.string.sp_contact), "")
            val deviceId = preferences.getString(activity.getString(R.string.sp_device_id), "")
            val data = Data.Builder()
                .putString(WorkerVars.KEY_APP_ID, appId)
                .putString(WorkerVars.KEY_APP_KEY, appKey)
                .putString(WorkerVars.KEY_CONTACT, contact)
                .putString(WorkerVars.KEY_DEVICE_ID, deviceId)
                .build()
            val request: WorkRequest =
                OneTimeWorkRequest.Builder(ClearWorker::class.java).setInputData(data).build()
            WorkManager.getInstance(activity).enqueue(request)
            clearPrefs(activity)
        }

        @JvmStatic
        fun handleNotification(svc: FirebaseMessagingService, remoteMessage: RemoteMessage) {}
        private fun doNothing(svc: FirebaseMessagingService, remoteMessage: RemoteMessage) {
            Log.d(TAG, "From: " + remoteMessage.from)
            if (remoteMessage.notification == null) {
                return
            }
            val messageBody = remoteMessage.notification!!.body
            Log.d(
                TAG,
                "Message Notification Body: $messageBody"
            )
            val intent = Intent(svc, AppInboxActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(
                svc, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT
            )
            val channelId = svc.getString(R.string.default_notification_channel_id)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(svc, channelId)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(remoteMessage.notification!!.title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
            val notificationManager =
                svc.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
        }

        private fun savePrefs(activity: Activity, appId: String, appKey: String, contact: String) {
            val sharedPref = activity.getSharedPreferences(
                activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE
            )
            val editor = sharedPref.edit()
            editor.putString(activity.getString(R.string.sp_app), appId)
            editor.putString(activity.getString(R.string.sp_key), appKey)
            editor.putString(activity.getString(R.string.sp_contact), contact)
            editor.apply()
        }

        private fun saveDeviceId(activity: Activity, deviceId: String?) {
            val sharedPref = activity.getSharedPreferences(
                activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE
            )
            val editor = sharedPref.edit()
            editor.putString(activity.getString(R.string.sp_device_id), deviceId)
            editor.apply()
        }

        private fun clearPrefs(activity: Activity) {
            val sharedPref = activity.getSharedPreferences(
                activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE
            )
            val editor = sharedPref.edit()
            editor.clear()
            editor.apply()
        }
    }
}