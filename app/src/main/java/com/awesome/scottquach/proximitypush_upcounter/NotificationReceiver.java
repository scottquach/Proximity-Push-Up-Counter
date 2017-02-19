package com.awesome.scottquach.proximitypush_upcounter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Scott Quach on 2/17/2017.
 */

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent openApp = new Intent(context, StartMenu.class);
        openApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 101, openApp, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification builder = new Notification.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Daily Push-Ups")
                .setContentText("This is your reminder to do Push-Ups!")
                .setAutoCancel(true)
                .build();
        notificationManager.notify(101, builder);
    }
}
