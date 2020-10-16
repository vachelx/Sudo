package com.hu.easycall.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.hu.easycall.MyApplication;
import com.hu.easycall.R;

public class NotificationUtil {
    private static final String WEB_SOCKET_CHANNEL_ID = "web_socket_channel_id";

    public static void createCommonNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "应用通知栏常驻";
            NotificationChannel channel = new NotificationChannel(WEB_SOCKET_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_MIN);
            NotificationManager manager = (NotificationManager) MyApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    public static Notification createServiceForegroundNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, WEB_SOCKET_CHANNEL_ID);
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, WEB_SOCKET_CHANNEL_ID);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setGroupSummary(false);
        builder.setOnlyAlertOnce(true);
        builder.setOngoing(true);
        builder.setAutoCancel(false);
        builder.setGroup("service_background");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setWhen(System.currentTimeMillis());
        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.notification_view);
        builder.setContent(view);
        builder.setPriority(NotificationManager.IMPORTANCE_DEFAULT);
        builder.setVisibility(NotificationCompat.VISIBILITY_SECRET);//锁屏不显示
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
        return notification;
    }
}
