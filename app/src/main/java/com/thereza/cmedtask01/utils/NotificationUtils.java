package com.thereza.cmedtask01.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.thereza.cmedtask01.R;
import com.thereza.cmedtask01.activity.MainActivity;
import com.thereza.cmedtask01.model.Download;

public class NotificationUtils {
    private static NotificationCompat.Builder notificationBuilder;
    private static NotificationManager notificationManager;

    public static void generateNotifcation(Context mContext){
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(mContext.getApplicationContext(), MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
                notificationIntent, 0);
        notificationBuilder = new NotificationCompat.Builder(mContext,"downloadFile")
                .setSmallIcon(R.drawable.ic_action_download)
                .setContentTitle("Download")
                .setContentText("Downloading File")
                .setAutoCancel(true);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationManager.notify(0, notificationBuilder.build());
    }

    public static void sendNotification(Download download, int totalFileSize){

        notificationBuilder.setProgress(100,download.getProgress(),false);
        notificationBuilder.setContentText("Downloading file "+ download.getCurrentFileSize() +"/"+totalFileSize +" MB");
        notificationManager.notify(0, notificationBuilder.build());
    }

    public static void onDownloadComplete(){

        notificationManager.cancel(0);
        notificationBuilder.setProgress(0,0,false);
        notificationBuilder.setContentText("File Downloaded");
        notificationManager.notify(0, notificationBuilder.build());

    }

    public static void cancelNotification(){
        notificationManager.cancel(0);
    }
}
