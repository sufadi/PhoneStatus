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
import com.tct.phonedata.bean.CustomSensorInfo;
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
    public static final String ACTION_RECORD_SENSOR_INFO = "action_record_sensor_info";


    private int mSceneMode;

    private SensorManager mSensorManager;
    private CustomSensorInfo mCustomSensorInfo;
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
        Log.d(MyConstant.TAG, "PhoneDataService onCreate()");
        initSensors();
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
        } else if(ACTION_RECORD_SENSOR_INFO.equals(mAction)) {
            recordAllSensorInfo();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(MyConstant.TAG, "PhoneDataService onDestroy()");
        mCustomSensorInfo = null;
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

    private Sensor mAccSensor;
    private Sensor mLineAccSensor;

    private void initSensors() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER, true);
        mLineAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION, true);
    }


    private void registerSensorListener(){
        Log.w(MyConstant.TAG, "registerSensorListener");
        mCustomSensorInfo = new CustomSensorInfo();
        mCustomSensorInfo.type = mSceneMode;
        mCustomSensorInfo.isFistLoading = true;

        //mSensorManager.registerListener(mSensorEventListener, mAccSensor, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(mSensorEventListener, mLineAccSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    private void unRegisterSensorListener(){
        Log.w(MyConstant.TAG, "unRegisterSensorListener");
        //mSensorManager.unregisterListener(mSensorEventListener, mAccSensor);
        mSensorManager.unregisterListener(mSensorEventListener, mLineAccSensor);
    }

    private SensorEventListener mSensorEventListener = new SensorEventListener() {


        @Override
        public void onSensorChanged(SensorEvent event) {
            int mSensorType = event.sensor.getType();
            switch (mSensorType) {
                case Sensor.TYPE_ACCELEROMETER:
                    Log.d(MyConstant.TAG, "onSensorChanged accelerometer size = " + event.values.length);
                    if (event.values.length == 3) {
                        mCustomSensorInfo.accelerometer_x = event.values[0];
                        mCustomSensorInfo.accelerometer_y = event.values[1];
                        mCustomSensorInfo.accelerometer_z = event.values[2];
                        Log.d(MyConstant.TAG, "onSensorChanged accelerometer (x,y,z) = (" + mCustomSensorInfo.accelerometer_x + " , "+ mCustomSensorInfo.accelerometer_y + " , "+ mCustomSensorInfo.accelerometer_z + " )");
                    } else {
                        Log.e(MyConstant.TAG, "Can't reach error AccSensor");
                    }
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    Log.d(MyConstant.TAG, "onSensorChanged line-accelerometer size = " + event.values.length);
                    if (event.values.length == 3) {
                        mCustomSensorInfo.linear_acceleration_x = event.values[0];
                        mCustomSensorInfo.linear_acceleration_y = event.values[1];
                        mCustomSensorInfo.linear_acceleration_z = event.values[2];
                        Log.d(MyConstant.TAG, "onSensorChanged line-accelerometer (x,y,z) = (" + mCustomSensorInfo.linear_acceleration_x + " , "+ mCustomSensorInfo.linear_acceleration_y + " , "+ mCustomSensorInfo.linear_acceleration_z + " )");
                    } else {
                        Log.e(MyConstant.TAG, "Can't reach error line-accelerometer");
                    }
                    break;
            }

            if (mCustomSensorInfo.isFistLoading) {
                mCustomSensorInfo.isFistLoading = false;
                createLogFileTitle();
            }

            DataToFileUtil.writeFileDataValue(mCustomSensorInfo.toString());
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void createLogFileTitle() {
        StringBuilder mSb = new StringBuilder();
        mSb.append("user_id");
        mSb.append(",");
        mSb.append("type");
        mSb.append(",");
        mSb.append("time");
        mSb.append(",");
        mSb.append("accelerometer_x");
        mSb.append(",");
        mSb.append("accelerometer_y");
        mSb.append(",");
        mSb.append("accelerometer_z");
        mSb.append(",");
        mSb.append("linear_acceleration_x");
        mSb.append(",");
        mSb.append("linear_acceleration_y");
        mSb.append(",");
        mSb.append("linear_acceleration_z");
        mSb.append(",");
        mSb.append("gyroscope_x");
        mSb.append(",");
        mSb.append("gyroscope_y");
        mSb.append(",");
        mSb.append("gyroscope_z");
        mSb.append(",");
        mSb.append("magnetometer_x");
        mSb.append(",");
        mSb.append("magnetometer_y");
        mSb.append(",");
        mSb.append("magnetometer_z");
        mSb.append(",");
        mSb.append("azimuth");
        mSb.append(",");
        mSb.append("pitch");
        mSb.append(",");
        mSb.append("roll");

        DataToFileUtil.writeFileDataValue(mSb.toString());
    }

}
