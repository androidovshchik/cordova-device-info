package ru.androidovshchik;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

@SuppressLint("NewApi")
public class NotificationService extends NotificationListenerService {

    private static final String TAG = "NS";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        logNotification(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }

    private void logNotification(StatusBarNotification sbn) {
        LogUtil.logDivider(TAG, ":");
        LogUtil.logCentered(" ", TAG, "New notification");
        LogUtil.logCentered(":", TAG, "packageName: " + sbn.getPackageName());
        LogUtil.logCentered(":", TAG, "id: " + sbn.getId());
        Notification notification = sbn.getNotification();
        if (notification.actions != null) {
            LogUtil.logCentered(" ", TAG, "Notification actions");
            for (Notification.Action action : notification.actions) {
                LogUtil.logCentered(":", TAG, "action.title: " + action.title);
            }
        }
        if (notification.extras != null) {
            LogUtil.logCentered(" ", TAG, "Notification extras");
            for (String key : notification.extras.keySet()) {
                LogUtil.logCentered(":", TAG, key + ": " + notification.extras.get(key));
            }
        }
        LogUtil.logDivider(TAG, ":");
    }
}
