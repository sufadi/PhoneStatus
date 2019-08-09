package com.tct.phonedata.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.tct.phonedata.utils.MyConstant;

public class PhoneDataService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(MyConstant.TAG, "PhoneDataService onCreate()");
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
        super.onDestroy();
        Log.d(MyConstant.TAG, "PhoneDataService onDestroy()");
    }
}
