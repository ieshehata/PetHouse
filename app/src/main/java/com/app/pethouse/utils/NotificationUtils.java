package com.app.pethouse.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.app.pethouse.R;

public class NotificationUtils {
    private static String TAG = NotificationUtils.class.getSimpleName();

    private Context mContext;
    String channelId = "ot-channel-01";

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    public void showNotification(String title, String content) {

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, channelId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Ramad System Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

            mBuilder.setSmallIcon(R.drawable.logo)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    //.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.logo))
                    .setContentText(content)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(content))
                    .setChannelId(channelId)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH);
        }
        else {
            mBuilder.setSmallIcon(R.drawable.logo)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    //.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.logo))
                    .setContentText(content)
                    .setChannelId(channelId)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        assert notificationManager != null;
        notificationManager.notify(SharedData.NOTIFICATION_ID, mBuilder.build());


    }

    public void showNotification(String title, String content, PendingIntent pIntent) {

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, channelId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "OT System Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

            mBuilder.setSmallIcon(R.drawable.logo)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    //.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.logo))
                    .setContentText(content)
                    .setContentIntent(pIntent)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(content))
                    .setChannelId(channelId)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH);
        }
        else {
            mBuilder.setSmallIcon(R.drawable.logo)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    //.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.logo))
                    .setContentText(content)
                    .setChannelId(channelId)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        assert notificationManager != null;
        notificationManager.notify(SharedData.NOTIFICATION_ID, mBuilder.build());


    }
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancelAll();
    }
}
