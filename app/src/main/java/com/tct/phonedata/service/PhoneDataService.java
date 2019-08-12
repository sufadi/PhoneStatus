package com.tct.phonedata.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.tct.phonedata.R;
import com.tct.phonedata.bean.SensorInfo;
import com.tct.phonedata.ui.MainActivity;
import com.tct.phonedata.utils.DataToFileUtil;
import com.tct.phonedata.utils.MyConstant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PhoneDataService extends Service {

    public static final int FORGROUND_ID = 0x88;

    public static final String ACTION_START_TEST = "action_start_test";
    public static final String ACTION_STOP_TEST = "action_stop_test";


    private SensorManager mSensorManager;
    private List<SensorInfo> mSensorList = new ArrayList<SensorInfo>();

    private PhoneDataBinder mPhoneDataBinder = new PhoneDataBinder();

    public class PhoneDataBinder extends Binder {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mPhoneDataBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(MyConstant.TAG, "PhoneDataService onCreate()");

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        initSensorListInfo();
        recordAllSensorInfo();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == intent) {
            return super.onStartCommand(intent, flags, startId);
        }
        String mAction = intent.getAction();
        Log.d(MyConstant.TAG, "PhoneDataService onStartCommand mAction : " + mAction);

        if (ACTION_START_TEST.equals(mAction)) {
            startMyForeground();
            Log.d(MyConstant.TAG, "startMyForeground show notification");

        } else if(ACTION_STOP_TEST.equals(mAction)) {
            stopForeground(true);
            Log.d(MyConstant.TAG, "stopForeground hide notification");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(MyConstant.TAG, "PhoneDataService onDestroy()");
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

    private void initSensorListInfo(){
        List<Sensor> list = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor mSensor: list) {
            SensorInfo mSensorInfo = new SensorInfo();
            mSensorInfo.name = mSensor.getName();
            mSensorInfo.type = mSensor.getType();
            mSensorInfo.description = mSensor.toString();

            mSensorList.add(mSensorInfo);
        }

        Collections.sort(mSensorList, mComparator);

    }

    Comparator<SensorInfo> mComparator = new Comparator<SensorInfo>() {
        @Override
        public int compare(SensorInfo o1, SensorInfo o2) {
            return o1.type - o2.type;
        }
    };

    private void recordAllSensorInfo(){
        if (DataToFileUtil.isFileSensorExit()) {
            return;
        }

        if (null != mSensorList && !mSensorList.isEmpty()){

            for (int i = 0; i < mSensorList.size(); i++) {
                Log.w(MyConstant.TAG, "recordAllSensorInfo:" + mSensorList.get(i).description);
                StringBuilder mSb = new StringBuilder();
                mSb.append("SensorType:");
                mSb.append(mSensorList.get(i).type);
                mSb.append(",Name: ");
                mSb.append(mSensorList.get(i).name);
                mSb.append(", Description: ");
                mSb.append(mSensorList.get(i).description);

                DataToFileUtil.writeFileSensorInfo(mSb.toString());
            }
        }
    }
}
