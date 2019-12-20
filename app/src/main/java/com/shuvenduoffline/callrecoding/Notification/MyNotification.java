package com.shuvenduoffline.callrecoding.Notification;

import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.shuvenduoffline.callrecoding.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MyNotification {
    public static Context ctx;
    public final static int RECORDING_NOTIFICATION = 1;
    public final static int PLAYING_NOTIFICATION = 2;

    public MyNotification(Context ctx) {
        this.ctx = ctx;
    }

    public void ShowNotificationForRecordOnging() {
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);
        builder.setSmallIcon(R.drawable.ic_phone_call);
        builder.setContentTitle(ctx.getString(R.string.notification_title));
        builder.setContentText(ctx.getString(R.string.notification_text));
        builder.setAutoCancel(true);
        builder.setOngoing(true);
        notificationManager.notify(RECORDING_NOTIFICATION, builder.build());

    }

    public void ShowNotificationForRecordPlaying() {
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);
        builder.setSmallIcon(R.drawable.ic_phone_call);
        builder.setContentTitle(ctx.getString(R.string.notification_record_playing));
        builder.setContentText(ctx.getString(R.string.notification_record_playing_decription));
        builder.setAutoCancel(true);
        builder.setOngoing(true);
        notificationManager.notify(PLAYING_NOTIFICATION, builder.build());
    }

    public void CancleNotification(int id) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(id);
    }

}
