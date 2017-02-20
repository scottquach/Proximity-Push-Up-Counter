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
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Scott Quach on 2/17/2017.
 */

public class NotificationReceiver extends BroadcastReceiver {


//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }

//    @Override
//    public void onCreate() {
//        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//        Notification mBuilder = new Notification.Builder(this)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("Title")
//                .setContentText("Text")
//                .build();
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0, mBuilder);
//
//    }


    @Override
    public void onReceive(Context context, Intent intent) {

        Intent openApp = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 , openApp, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification mBuilder = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Do Push-Ups!")
                .setContentText("This is your daily reminder")
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, mBuilder);

    }
}
