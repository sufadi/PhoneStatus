package com.tct.phonedata.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.tct.phonedata.R;
import com.tct.phonedata.bean.SensorInfoSorted;
import com.tct.phonedata.ui.MainActivity;
import com.tct.phonedata.utils.DataToFileUtil;
import com.tct.phonedata.utils.MyConstant;
import com.tct.phonedata.utils.UuidUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PhoneDataService extends Service {

    public static final int FORGROUND_ID = 0x88;

    public static final String ACTION_START_TEST = "action_start_test";
    public static final String ACTION_STOP_TEST = "action_stop_test";


    private String mUUID;
    private int mSceneMode;

    private SensorManager mSensorManager;

    private PhoneDataBinder mPhoneDataBinder = new PhoneDataBinder();

    public class PhoneDataBinder extends Binder {

        public String getResultFilePath(){
            return DataToFileUtil.FILE_PATH;
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mPhoneDataBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mUUID = UuidUtils.getDeviceUUID(this);
        Log.d(MyConstant.TAG, "PhoneDataService onCreate() + UUID:" + mUUID);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
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
            mSceneMode = intent.getIntExtra(MainActivity.KEY_SCENE_MODE, 0);
            Log.d(MyConstant.TAG, "PhoneDataService SceneMode = " + mSceneMode);

            startMyForeground();

            registerSensorListener();
        } else if(ACTION_STOP_TEST.equals(mAction)) {
            stopForeground(true);

            unRegisterSensorListener();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(MyConstant.TAG, "PhoneDataService onDestroy()");
        super.onDestroy();
    }

    private void startMyForeground() {
        Log.d(MyConstant.TAG, "startMyForeground show notification");
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

    Comparator<SensorInfoSorted> mComparator = new Comparator<SensorInfoSorted>() {
        @Override
        public int compare(SensorInfoSorted o1, SensorInfoSorted o2) {
            return o1.type - o2.type;
        }
    };

    private void recordAllSensorInfo(){
        if (DataToFileUtil.isFileSensorExit()) {
            return;
        }
        List<SensorInfoSorted> mSensorList = new ArrayList<SensorInfoSorted>();

        List<Sensor> list = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor mSensor: list) {
            SensorInfoSorted mSensorInfo = new SensorInfoSorted();
            mSensorInfo.name = mSensor.getName();
            mSensorInfo.type = mSensor.getType();
            mSensorInfo.description = mSensor.toString();

            mSensorList.add(mSensorInfo);
        }

        Collections.sort(mSensorList, mComparator);

        if (null != mSensorList && !mSensorList.isEmpty()){

            for (int i = 0; i < mSensorList.size(); i++) {
                Log.w(MyConstant.TAG, "recordAllSensorInfo:" + mSensorList.get(i).description);
                StringBuilder mSb = new StringBuilder();
                mSb.append("SensorType: ");
                mSb.append(mSensorList.get(i).type);
                mSb.append(", Name: ");
                mSb.append(mSensorList.get(i).name);
                mSb.append(", Description: ");
                mSb.append(mSensorList.get(i).description);

                DataToFileUtil.writeFileSensorInfo(mSb.toString());
            }
        }
    }

    private void registerSensorListener(){
        Log.w(MyConstant.TAG, "registerSensorListener");
    }

    private void unRegisterSensorListener(){
        Log.w(MyConstant.TAG, "unRegisterSensorListener");

    }

    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

}
