package com.tct.phonedata.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.tct.phonedata.R;
import com.tct.phonedata.ui.MainActivity;
import com.tct.phonedata.utils.MyConstant;

public class PhoneDataService extends Service {


    public static final int FORGROUND_ID = 0x88;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(MyConstant.TAG, "PhoneDataService onCreate()");
        startMyForeground();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        String mAction = intent.getAction();
        Log.d(MyConstant.TAG, "PhoneDataService onStartCommand mAction : " + mAction);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(MyConstant.TAG, "PhoneDataService onDestroy()");
        stopForeground(true);
        super.onDestroy();
    }

    private void startMyForeground() {
        Log.d(MyConstant.TAG, "PhoneDataService startMyForeground sdk :" + android.os.Build.VERSION.SDK_INT);
        Notification.Builder nb =  new Notification.Builder(this);

        if (android.os.Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ONE_ID = "channel_id_foreground";
            String CHANNEL_ONE_NAME = "Channel One";
            NotificationChannel notificationChannel = null;

            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_LOW);

            nb.setChannelId(CHANNEL_ONE_ID);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }

        nb.setSmallIcon(R.mipmap.ic_launcher);
        nb.setContentTitle(getString(R.string.notification_title));
        nb.setContentText(getString(R.string.notification_Content));
        nb.setContentIntent(PendingIntent.getActivity(this,0, new Intent(this, MainActivity.class),0));

        try {
            startForeground(FORGROUND_ID, nb.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
