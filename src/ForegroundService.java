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
// import android.annotation.TargetApi;

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

        // Delete notification channel if it already exists
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.deleteNotificationChannel("foreground.service.channel");

        // Get notification channel importance
        Integer importance;

        try {
            importance = Integer.parseInt((String) extras.get("importance"));
        } catch (NumberFormatException e) {
            importance = 1;
        }

        switch(importance) {
            case 2:
                importance = NotificationManagerCompat.IMPORTANCE_DEFAULT;
                break;
            case 3:
                importance = NotificationManagerCompat.IMPORTANCE_HIGH;
                break;
            default:
                importance = NotificationManagerCompat.IMPORTANCE_LOW;
            // We are not using IMPORTANCE_MIN because we want the notification to be visible
        }

        // Get notification icon
        int icon = getResources().getIdentifier((String) extras.get("icon"), "drawable", context.getPackageName());
        
        
        NotificationChannel channel;
        Notification notification;
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            // Create notification channel
            channel = new NotificationChannel("foreground.service.channel", "Background Services", importance);
            channel.setDescription("Enables background processing.");
            getSystemService(NotificationManager.class).createNotificationChannel(channel);

            // Make notification
            notification = new NotificationCompat.Builder(context, "foreground.service.channel")
                .setContentTitle((CharSequence) extras.get("title"))
                .setContentText((CharSequence) extras.get("text"))
                .setOngoing(true)
                .setSmallIcon(icon == 0 ? 17301514 : icon) // Default is the star icon
                .build();
        } else {
            // Make notification
            notification = new NotificationCompat.Builder(context)
                .setContentTitle((CharSequence) extras.get("title"))
                .setContentText((CharSequence) extras.get("text"))
                .setOngoing(true)
                .setSmallIcon(icon == 0 ? 17301514 : icon) // Default is the star icon
                .build();
        }

        // Get notification ID
        Integer id;
        try {
            id = Integer.parseInt((String) extras.get("id"));
        } catch (NumberFormatException e) {
            id = 0;
        }

        // Put service in foreground and show notification (id of 0 is not allowed)
        startForeground(id != 0 ? id : 197812504, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
