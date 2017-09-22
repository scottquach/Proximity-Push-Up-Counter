package com.awesome.scottquach.proximitypush_upcounter;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Scott Quach on 2/17/2017.
 */

public class NotificationReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        //PendingIntent to open app when notification is clicked
        Intent openApp = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 , openApp, PendingIntent.FLAG_CANCEL_CURRENT);
        //Retreive default notification sound
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //Create notification
        Notification mBuilder = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Do Push-Ups!")
                .setContentText("This is your daily reminder")
                .setContentIntent(pendingIntent)
                .setSound(sound)
                .setAutoCancel(true)
                .build();

        //Notify
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(101, mBuilder);

    }
}
