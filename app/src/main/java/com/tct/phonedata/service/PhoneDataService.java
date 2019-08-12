package com.tct.phonedata.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.tct.phonedata.R;
import com.tct.phonedata.bean.SensorInfo;
import com.tct.phonedata.bean.SensorInfoSorted;
import com.tct.phonedata.ui.MainActivity;
import com.tct.phonedata.utils.DataToFileUtil;
import com.tct.phonedata.utils.DateTimeUtil;
import com.tct.phonedata.utils.MyConstant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneDataService extends Service {

    public static final int FORGROUND_ID = 0x88;

    public static final String ACTION_START_TEST = "action_start_test";
    public static final String ACTION_STOP_TEST = "action_stop_test";


    private boolean isScreenOn;
    private String mFilePath;
    private HashMap<String, SensorInfo> mMaps;

    private SensorManager mSensorManager;

    private PhoneDataBroadCast mPhoneDataBroadCast;
    private PhoneDataBinder mPhoneDataBinder = new PhoneDataBinder();

    public class PhoneDataBinder extends Binder {

        public String getResultFilePath(){
            return mFilePath;
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mPhoneDataBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(MyConstant.TAG, "PhoneDataService onCreate()");

        PowerManager mPm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        isScreenOn = mPm.isInteractive();

        mPhoneDataBroadCast = new PhoneDataBroadCast(this);
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
            mFilePath = DataToFileUtil.FILE_PATH  + DateTimeUtil.getFileSysTime();

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

    private void initSensorListInfo(){
        mMaps = new HashMap<String, SensorInfo>();
        List<Sensor> list = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        for (Sensor mSensor: list) {
            SensorInfo mSensorInfo = new SensorInfo();
            mSensorInfo.name = mSensor.getName();
            mSensorInfo.type = mSensor.getType();
            mSensorInfo.sensor = mSensor;

            mMaps.put(mSensorInfo.name, mSensorInfo);
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

        if (null != mMaps && !mMaps.isEmpty()) {
            for (Map.Entry<String, SensorInfo> entry : mMaps.entrySet()) {
                // filter non-wakeup sensor
                if (entry.getValue().name.contains("Non-wakeup")) {
                    Log.w(MyConstant.TAG, "registerSensorListener filter non-wakeup sensor " + entry.getValue().name);
                    continue;
                }

                entry.getValue().isFistLoading = true;
                mSensorManager.registerListener(mSensorEventListener, entry.getValue().sensor, SensorManager.SENSOR_DELAY_GAME);
            }
        }
    }

    private void unRegisterSensorListener(){
        Log.w(MyConstant.TAG, "unRegisterSensorListener");
        if (null != mMaps && !mMaps.isEmpty()) {
            for (Map.Entry<String, SensorInfo> entry : mMaps.entrySet()) {
                if (entry.getValue().name.contains("Non-wakeup")) {
                    Log.w(MyConstant.TAG, "unRegisterSensorListener filter non-wakeup sensor " + entry.getValue().name);
                    continue;
                }

                entry.getValue().isFistLoading = false;
                entry.getValue().fileName = null;
                mSensorManager.unregisterListener(mSensorEventListener, entry.getValue().sensor);
            }
        }
    }

    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (null == event || null == event.sensor) {
                return;
            }

            String name = event.sensor.getName();
            float[] mResult = event.values;

            if (mMaps.containsKey(name)) {
                if (mMaps.get(name).isFistLoading) {
                    // creat file name and file title
                    mMaps.get(name).fileName = Build.BRAND + "_" + name + "_value.log";
                    DataToFileUtil.writeFile(mFilePath, mMaps.get(name).fileName, createTile(event.values.length));
                    mMaps.get(name).isFistLoading = false;

                    Log.w(MyConstant.TAG, mFilePath + "/fileName : " + mMaps.get(name).fileName);
                }

                if (mMaps.get(name).fileName != null) {
                    StringBuilder mSb = new StringBuilder();
                    mSb.append(DateTimeUtil.getSysTime());
                    mSb.append(",");
                    mSb.append(isScreenOn ? "on":"off");

                    for (int i = 0; i < mResult.length; i++) {
                        mSb.append(",");
                        mSb.append(mResult[i]);
                    }

                    DataToFileUtil.writeFile(mFilePath, mMaps.get(name).fileName, mSb.toString());
                    Log.d(MyConstant.TAG, "name : " + name + ", result :" + mSb.toString());
                }
            } else {
                Log.e(MyConstant.TAG, "Can't reach name = " + name);
            }



        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private String createTile(int resultSize) {
        String title = "Time" + "    Screen";
        for (int i = 0; i < resultSize; i++) {
            title = title + "    Value["+ i + "]";
        }
        Log.d(MyConstant.TAG, "createTile : " + title);
        return title;
    }


    private class PhoneDataBroadCast extends BroadcastReceiver {

        public PhoneDataBroadCast(Context mContext){
            IntentFilter mIt = new IntentFilter();
            mIt.addAction(Intent.ACTION_SCREEN_OFF);
            mIt.addAction(Intent.ACTION_SCREEN_ON);
            mContext.registerReceiver(this, mIt);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent) {
                return;
            }

            String mAction = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(mAction)) {
                isScreenOn = true;
                Log.d(MyConstant.TAG, "screen on");
            } else if (Intent.ACTION_SCREEN_OFF.equals(mAction)) {
                isScreenOn = false;
                Log.d(MyConstant.TAG, "screen off");
            }
        }
    }
}
