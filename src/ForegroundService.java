package com.davidbriglio.foreground;

import android.content.Intent;
import android.content.Context;
import android.app.Service;
import android.app.Notification;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.support.v4.app.NotificationManagerCompat;
import android.os.IBinder;
import android.os.Bundle;
import android.annotation.TargetApi;

public class ForegroundService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals("start")) {
            // Start the service
            startPluginForegroundService(intent.getExtras());
        } else {
            // Stop the service
            stopForeground(true);
            stopSelf();
        }

        return START_STICKY;
    }

//     @TargetApi(26)
    private void startPluginForegroundService(Bundle extras) {
        Context context = getApplicationContext();
        ///////////////////////////////////////////////
        // Get notification icon
        int icon = getResources().getIdentifier((String) extras.get("icon"), "drawable", context.getPackageName());

        // Get notification ID
        Integer id;
        try {
            id = Integer.parseInt((String) extras.get("id"));
        } catch (NumberFormatException e) {
            id = 0;
        }

        final NotificationCompat.Builder builder = getNotificationBuilder(context,
             "com.example.your_app.notification.CHANNEL_ID_FOREGROUND", // Channel id
        NotificationManagerCompat.IMPORTANCE_LOW); //Low importance prevent visual appearance for this notification channel on top
        builder.setOngoing(true)
        .setSmallIcon(icon == 0 ? 17301514 : icon)
        .setContentTitle((CharSequence) extras.get("title"))
        .setContentText((CharSequence) extras.get("text"));

        Notification notification = builder.build();

        startForeground(id, notification);

        // if (notificationId != lastShownNotificationId) {
        //       // Cancel previous notification
        //       final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //       nm.cancel(lastShownNotificationId);
        // }
        // lastShownNotificationId = notificationId;
        ///////////////////////////////////////////////
    }

    public static NotificationCompat.Builder getNotificationBuilder(Context context, String channelId, int importance) {
        NotificationCompat.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            prepareChannel(context, channelId, importance);
            builder = new NotificationCompat.Builder(context, channelId);
        } else {
            builder = new NotificationCompat.Builder(context);
        }
        return builder;
    }

    @TargetApi(26)
    private static void prepareChannel(Context context, String id, int importance) {
        final String appName = "Background Services";
        String description = "Enables background processing.";
        final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(nm != null) {
            NotificationChannel nChannel = nm.getNotificationChannel(id);

            if (nChannel == null) {
                nChannel = new NotificationChannel(id, appName, importance);
                nChannel.setDescription(description);
                nm.createNotificationChannel(nChannel);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
