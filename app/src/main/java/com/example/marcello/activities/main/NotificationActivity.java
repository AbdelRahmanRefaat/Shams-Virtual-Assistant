package com.example.marcello.activities.main;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
public class NotificationActivity extends Activity {

    public static final String TAG = "MainActivity";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS =
            "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private static final String WA_PACKAGE = "com.whatsapp";
    TextView notifications;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notifications = (TextView) findViewById(R.id.notificationtxtview);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onStart() {
        showNotifications();
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResume() {
        showNotifications();
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void showNotifications() {
        if (isNotificationServiceEnabled()) {
            Log.i(TAG, "Notification enabled -- trying to fetch it");
            getNotifications();
        } else {
            Log.i(TAG, "Notification disabled -- Opening settings");
            startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void getNotifications() {
        StringBuilder st = new StringBuilder();
        Log.i(TAG, "Waiting for MyNotificationService");
        NotificationService myNotificationService = NotificationService.get();
        Log.i(TAG, "Active Notifications: [");
        st.append("Active Notifications: ").append("\r\n");
        for (StatusBarNotification notification :
                myNotificationService.getActiveNotifications()) {
            if (notification.getPackageName().startsWith("com.android")
                    ||notification.getPackageName().startsWith("com.samsung.android")) continue;
            if (notification.getPackageName().startsWith("com.estrongs")) continue;
            if (notification.getPackageName().startsWith("com.motorola")) continue;
            if (notification.getPackageName().startsWith("Screenshot")) continue;

            Notification notification1 = notification.getNotification();
            Bundle bun = notification1.extras;


            if (bun.getString(Notification.EXTRA_BIG_TEXT) != null)
                st.append(bun.getString(Notification.EXTRA_BIG_TEXT)
                        .toString()).append("\n");
            if (bun.getString(Notification.EXTRA_SUMMARY_TEXT) != null)
                st.append(bun.getString(Notification.EXTRA_SUMMARY_TEXT)
                        .toString()).append("\n");
            if (bun.getString(Notification.EXTRA_INFO_TEXT) != null)
                st.append(bun.getString(Notification.EXTRA_INFO_TEXT)
                        .toString()).append("\n");

            if (bun.getString(Notification.EXTRA_SUB_TEXT) != null)
                st.append(bun.getString(Notification.EXTRA_SUB_TEXT)
                        .toString()).append("\n");
            if (bun.getString(Notification.EXTRA_TITLE_BIG) != null)
                st.append(bun.getString(Notification.EXTRA_TITLE_BIG)
                        .toString()).append("\n");
            CharSequence[] lines = bun.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
            if (lines != null) {
                for (CharSequence line : lines) {
                    st.append(line.toString()).append("  \n");
                }
            }

            //String from = bundle.getString(NotificationCompat.EXTRA_TITLE);
            //String message = bundle.getString(NotificationCompat.EXTRA_TEXT);

            //st.append("From: ").append(from);
            //st.append(message).append("\r\n");

            //SimpleDateFormat format = new SimpleDateFormat("DD-kk:mm:ss:SSS");
            //Long ptime = notification.getPostTime();
            //st.append("Post time: ").append(format.format(ptime)).append("\n");
            //Long nottime = notification.when;
            //st.append("When: ").append(format.format(nottime)).append("\n");
            //Log.i(TAG, "From: " + from);
            //Log.i(TAG, "Message: " + message);

        }
        st.append("]");
        notifications.setText(st);
        Log.i(TAG,notifications.getText().toString());
    }

    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String allNames = Settings.Secure.getString(getContentResolver(),
                "enabled_notification_listeners");
        if (allNames != null && !allNames.isEmpty()) {
            for (String name : allNames.split(":")) {
                if (getPackageName().equals(
                        ComponentName.unflattenFromString(name).getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }


}
