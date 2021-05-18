package com.appinbox.sdk;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.appinbox.sdk.msg.ListActivity;
import com.appinbox.sdk.worker.ClearWorker;
import com.appinbox.sdk.worker.InitWorker;
import com.appinbox.sdk.worker.WorkerVars;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.content.Context.MODE_PRIVATE;

public class AppInboxSDK {
    private static final String TAG = "SDK";
    public static final String SDK_VERSION = "1.5.18";

    public static void init(@NonNull Activity activity, String appId, String appKey, String contact) {
        savePrefs(activity, appId, appKey, contact);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    String TAG = "SDK:FBSVC";
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    saveDeviceId(activity, task.getResult());
                    Data data = new Data.Builder()
                            .putString(WorkerVars.KEY_APP_ID, appId)
                            .putString(WorkerVars.KEY_APP_KEY, appKey)
                            .putString(WorkerVars.KEY_CONTACT, contact)
                            .putString(WorkerVars.KEY_DEVICE_ID, task.getResult())
                            .build();
                    WorkRequest request = new OneTimeWorkRequest.Builder(InitWorker.class).setInputData(data).build();
                    WorkManager.getInstance(activity).enqueue(request);
                });
    }

    public static void open(@NonNull Activity activity, String appId, String appKey, String contact) {
        savePrefs(activity, appId, appKey, contact);
        Intent i = new Intent(activity, ListActivity.class);
        activity.startActivity(i);
    }

    public static void logout(@NonNull Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences(activity.getString(R.string.preference_file_key), MODE_PRIVATE);
        String appId = preferences.getString(activity.getString(R.string.sp_app), "");
        String appKey = preferences.getString(activity.getString(R.string.sp_key), "");
        String contact = preferences.getString(activity.getString(R.string.sp_contact), "");
        String deviceId = preferences.getString(activity.getString(R.string.sp_device_id), "");
        Data data = new Data.Builder()
                .putString(WorkerVars.KEY_APP_ID, appId)
                .putString(WorkerVars.KEY_APP_KEY, appKey)
                .putString(WorkerVars.KEY_CONTACT, contact)
                .putString(WorkerVars.KEY_DEVICE_ID, deviceId)
                .build();
        WorkRequest request = new OneTimeWorkRequest.Builder(ClearWorker.class).setInputData(data).build();
        WorkManager.getInstance(activity).enqueue(request);
        clearPrefs(activity);
    }

    private void showFBToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    String TAG = "SDK:FBSVC";
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                });
    }

    public static void handleNotification(@NonNull FirebaseMessagingService svc, @NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getNotification() == null) {
            return;
        }
        String messageBody = remoteMessage.getNotification().getBody();
        Log.d(TAG, "Message Notification Body: " + messageBody);

        Intent intent = new Intent(svc, ListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(svc, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = svc.getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(svc, channelId)
                        .setSmallIcon(R.drawable.ic_stat_ic_notification)
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) svc.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private static void savePrefs(@NonNull Activity activity, String appId, String appKey, String contact) {
        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getString(R.string.preference_file_key), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(activity.getString(R.string.sp_app), appId);
        editor.putString(activity.getString(R.string.sp_key), appKey);
        editor.putString(activity.getString(R.string.sp_contact), contact);
        editor.apply();
    }
    private static void saveDeviceId(@NonNull Activity activity, String deviceId) {
        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getString(R.string.preference_file_key), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(activity.getString(R.string.sp_device_id), deviceId);
        editor.apply();
    }

    private static void clearPrefs(@NonNull Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getString(R.string.preference_file_key), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }
}
