package com.thdz.fast.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.thdz.fast.R;
import com.thdz.fast.app.MyApplication;


/**
 * 通知栏工具类
 */
public class NotifyUtil {

    public static void showNotification(Context context, String title, String content, Intent intent, int notyId) {
        // 获取一个 NotificationCompat.Builder 实例。
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), "default");
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        // 震动通知
        long[] vibratePattern = new long[]{400, 800, 1200, 1600};
        // 创建一个 Notification
        Notification notification = builder
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pi)
                .setColor(Color.WHITE)
                .setSmallIcon(R.drawable.push) // push_small
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL; // 设置点击后自动消失
        // 使用 NotificationManagerCompat 的 notify 方法展示你设置了 id 的那个 Notification
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);
        notificationManager.notify(notyId, notification);

    }


    /**
     * 清空通知栏消息
     */
    public static void clearNotification() {
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(MyApplication.getInstance());
        notificationManager.cancelAll();
    }

    /**
     * 点击消失的通知
     */
    public static void showNullNotify(Context context, String title, String content, int notyId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext());
        Notification notification = builder
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(null)
                .setColor(context.getApplicationContext().getResources().getColor(R.color.white))
                .setSmallIcon(R.drawable.push_small)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL; // 设置点击后自动消失
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context.getApplicationContext());
        notificationManager.notify(notyId, notification);
    }

}
