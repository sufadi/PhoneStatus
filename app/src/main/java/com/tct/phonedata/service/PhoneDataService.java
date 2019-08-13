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
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.tct.phonedata.R;
import com.tct.phonedata.bean.CustomSensorInfo;
import com.tct.phonedata.bean.SensorInfoSorted;
import com.tct.phonedata.ui.MainActivity;
import com.tct.phonedata.utils.DataToFileUtil;
import com.tct.phonedata.utils.DateTimeUtil;
import com.tct.phonedata.utils.MyConstant;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.tct.phonedata.utils.MyConstant.TAG;

public class PhoneDataService extends Service {

    public static final int FORGROUND_ID = 0x88;

    public static final String ACTION_START_TEST = "action_start_test";
    public static final String ACTION_STOP_TEST = "action_stop_test";
    public static final String ACTION_RECORD_SENSOR_INFO = "action_record_sensor_info";

    private static final int MSG_VIRTUAL_ORIENTATION_SENSOR_CAL = 0;

    private String mFileName;

    private int mSceneMode;

    //记录 rotationMatrix 矩阵值
    private float[] mRotationMatrixF = new float[9];
    //记录通过getOrientation()计算出来的方位横滚俯仰值
    private float[] mVirtualOrientationF = new float[3];
    private float[] mAccF = null;
    private float[] mMagneticF = null;

    private Sensor mAccSensor;
    private Sensor mLineAccSensor;
    private Sensor mGyroSensor;
    private Sensor mMagneticSensor;
    private Sensor mOrientationSensor;
    private boolean isVirtualOrientationSensorSupport;

    private MainHandle mMainHandle;
    private SensorManager mSensorManager;
    private CustomSensorInfo mCustomSensorInfo;
    private PhoneDataBinder mPhoneDataBinder = new PhoneDataBinder();

    private static class MainHandle extends Handler {

        private final WeakReference<PhoneDataService> mRefService;

        public MainHandle(PhoneDataService service) {
            mRefService = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            PhoneDataService service = mRefService.get();
            if (service == null) {
                return;
            }

            switch (msg.what) {
                case MSG_VIRTUAL_ORIENTATION_SENSOR_CAL:

                    if (service.mAccF != null && service.mMagneticF != null) {
                        if (SensorManager.getRotationMatrix(service.mRotationMatrixF, null, service.mAccF, service.mMagneticF)) {
                            SensorManager.getOrientation(service.mRotationMatrixF, service.mVirtualOrientationF);
                            if (service.mVirtualOrientationF.length == 3) {
                                service.mCustomSensorInfo.azimuth = service.mVirtualOrientationF[0];
                                service.mCustomSensorInfo.pitch = service.mVirtualOrientationF[1];
                                service.mCustomSensorInfo.roll = service.mVirtualOrientationF[2];

                                service.recordSensorDataToLogFile(service.mCustomSensorInfo);
                                Log.d(MyConstant.TAG, "onSensorChanged virtualOrientationF(x,y,z) = (" + service.mCustomSensorInfo.azimuth + " , "+ service.mCustomSensorInfo.pitch + " , "+ service.mCustomSensorInfo.roll + " )");
                            } else {
                                Log.e(MyConstant.TAG, "error can not reach leanth = " + service.mVirtualOrientationF.length);
                            }
                        }
                    }
                    break;
            }

        }
    }

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
        Log.d(TAG, "PhoneDataService onCreate()");

        mMainHandle = new MainHandle(PhoneDataService.this);

        initSensors();
        recordAllSensorInfo();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == intent) {
            return super.onStartCommand(intent, flags, startId);
        }
        String mAction = intent.getAction();
        Log.d(TAG, "PhoneDataService onStartCommand mAction : " + mAction);

        if (ACTION_START_TEST.equals(mAction)) {
            mSceneMode = intent.getIntExtra(MainActivity.KEY_SCENE_MODE, 0);
            Log.d(TAG, "PhoneDataService SceneMode = " + mSceneMode);

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
        Log.d(TAG, "PhoneDataService onDestroy()");
        mCustomSensorInfo = null;
        super.onDestroy();
    }

    private void startMyForeground() {
        Log.d(TAG, "startMyForeground show notification");
        Log.d(TAG, "PhoneDataService startMyForeground sdk :" + android.os.Build.VERSION.SDK_INT);
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
                Log.w(TAG, "recordAllSensorInfo:" + mSensorList.get(i).description);
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

    private void initSensors() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER, true);
        mLineAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION, true);
        mGyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE, true);
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD, true);
        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION, true);
    }


    private void registerSensorListener(){
        Log.w(TAG, "registerSensorListener");
        mAccF = null;
        mMagneticF = null;
        mFileName = Build.BRAND + "_" + DateTimeUtil.getFileSysTime() + "_Data_value.log";

        mCustomSensorInfo = new CustomSensorInfo();
        mCustomSensorInfo.type = mSceneMode;
        mCustomSensorInfo.isFistLoading = true;

        if (null != mAccSensor) {
            mSensorManager.registerListener(mSensorEventListener, mAccSensor, SensorManager.SENSOR_DELAY_GAME);
        } else {
            Log.w(TAG, "mAccSensor sensor is non-existent");
        }

        if (null != mLineAccSensor) {
            mSensorManager.registerListener(mSensorEventListener, mLineAccSensor, SensorManager.SENSOR_DELAY_GAME);
        } else {
            Log.w(TAG, "mLineAccSensor sensor is non-existent");
        }

        if (null != mGyroSensor) {
            mSensorManager.registerListener(mSensorEventListener, mGyroSensor, SensorManager.SENSOR_DELAY_GAME);
        } else {
            Log.w(TAG, "mGyroSensor sensor is non-existent");
        }

        if(null != mMagneticSensor) {
            mSensorManager.registerListener(mSensorEventListener, mMagneticSensor, SensorManager.SENSOR_DELAY_GAME);
        } else {
            Log.w(TAG, "mMagneticSensor sensor is non-existent");
        }

        if (mOrientationSensor != null) {
            mSensorManager.registerListener(mSensorEventListener, mOrientationSensor, SensorManager.SENSOR_DELAY_GAME);
            isVirtualOrientationSensorSupport = false;
        } else {
            Log.w(TAG, "orientation sensor is non-existent");
            if (mMagneticSensor != null && mAccSensor != null) {
                isVirtualOrientationSensorSupport = true;
                Log.w(TAG, "virual orientation sensor is Support!!!");
            }
        }
    }

    private void unRegisterSensorListener(){
        Log.w(TAG, "unRegisterSensorListener");
        if (null != mAccSensor) {
            mSensorManager.unregisterListener(mSensorEventListener, mAccSensor);
        }

        if (null != mLineAccSensor) {
            mSensorManager.unregisterListener(mSensorEventListener, mLineAccSensor);
        }

        if (null != mGyroSensor) {
            mSensorManager.unregisterListener(mSensorEventListener, mGyroSensor);
        }

        if (null != mMagneticSensor) {
            mSensorManager.unregisterListener(mSensorEventListener, mMagneticSensor);
        }

        if (mOrientationSensor != null ) {
            mSensorManager.unregisterListener(mSensorEventListener, mOrientationSensor);
        }

    }

    private SensorEventListener mSensorEventListener = new SensorEventListener() {


        @Override
        public void onSensorChanged(SensorEvent event) {
            int mSensorType = event.sensor.getType();
            switch (mSensorType) {
                case Sensor.TYPE_ACCELEROMETER:
                    if (event.values.length == 3) {
                        mCustomSensorInfo.accelerometer_x = event.values[0];
                        mCustomSensorInfo.accelerometer_y = event.values[1];
                        mCustomSensorInfo.accelerometer_z = event.values[2];

                        if (isVirtualOrientationSensorSupport) {
                            mAccF = event.values;
                            mMainHandle.sendEmptyMessage(MSG_VIRTUAL_ORIENTATION_SENSOR_CAL);
                        }
                        Log.d(TAG, "onSensorChanged accelerometer (x,y,z) = (" + mCustomSensorInfo.accelerometer_x + " , "+ mCustomSensorInfo.accelerometer_y + " , "+ mCustomSensorInfo.accelerometer_z + " )");
                    } else {
                        Log.e(TAG, "Can't reach error AccSensor size" + event.values.length);
                    }
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    if (event.values.length == 3) {
                        mCustomSensorInfo.linear_acceleration_x = event.values[0];
                        mCustomSensorInfo.linear_acceleration_y = event.values[1];
                        mCustomSensorInfo.linear_acceleration_z = event.values[2];
                        Log.d(TAG, "onSensorChanged line-accelerometer (x,y,z) = (" + mCustomSensorInfo.linear_acceleration_x + " , "+ mCustomSensorInfo.linear_acceleration_y + " , "+ mCustomSensorInfo.linear_acceleration_z + " )");
                    } else {
                        Log.e(TAG, "Can't reach error line-accelerometer size" + event.values.length);
                    }
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    if (event.values.length == 3) {
                        mCustomSensorInfo.gyroscope_x = event.values[0];
                        mCustomSensorInfo.gyroscope_y = event.values[1];
                        mCustomSensorInfo.gyroscope_z = event.values[2];
                        Log.d(TAG, "onSensorChanged gyro(x,y,z) = (" + mCustomSensorInfo.gyroscope_x + " , "+ mCustomSensorInfo.gyroscope_y + " , "+ mCustomSensorInfo.gyroscope_z + " )");
                    } else {
                        Log.e(TAG, "Can't reach error gyro size" + event.values.length);
                    }
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    if (event.values.length == 3) {
                        mCustomSensorInfo.magnetometer_x = event.values[0];
                        mCustomSensorInfo.magnetometer_y = event.values[1];
                        mCustomSensorInfo.magnetometer_z = event.values[2];
                        Log.d(TAG, "onSensorChanged magnetic(x,y,z) = (" + mCustomSensorInfo.magnetometer_x + " , "+ mCustomSensorInfo.magnetometer_y + " , "+ mCustomSensorInfo.magnetometer_z + " )");

                        if (isVirtualOrientationSensorSupport) {
                            mMagneticF = event.values;
                            mMainHandle.sendEmptyMessage(MSG_VIRTUAL_ORIENTATION_SENSOR_CAL);
                        }
                    } else {
                        Log.e(TAG, "Can't reach error magnetic size" + event.values.length);
                    }
                    break;
                case Sensor.TYPE_ORIENTATION:
                    if (event.values.length == 3) {
                        mCustomSensorInfo.azimuth = event.values[0];
                        mCustomSensorInfo.pitch = event.values[1];
                        mCustomSensorInfo.roll = event.values[2];
                        Log.d(TAG, "onSensorChanged orientation(x,y,z) = (" + mCustomSensorInfo.azimuth + " , "+ mCustomSensorInfo.pitch + " , "+ mCustomSensorInfo.roll + " )");
                    } else {
                        Log.e(TAG, "Can't reach error orientation size" + event.values.length);
                    }
                    break;
            }

            if (mCustomSensorInfo.isFistLoading) {
                mCustomSensorInfo.isFistLoading = false;
                createLogFileTitle();
            }

            recordSensorDataToLogFile(mCustomSensorInfo);
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

        if (mAccSensor != null) {
            mSb.append(",");
            mSb.append("accelerometer_x");
            mSb.append(",");
            mSb.append("accelerometer_y");
            mSb.append(",");
            mSb.append("accelerometer_z");
        }

        if(mLineAccSensor != null) {
            mSb.append(",");
            mSb.append("linear_acceleration_x");
            mSb.append(",");
            mSb.append("linear_acceleration_y");
            mSb.append(",");
            mSb.append("linear_acceleration_z");
        }

        if (mGyroSensor != null) {
            mSb.append(",");
            mSb.append("gyroscope_x");
            mSb.append(",");
            mSb.append("gyroscope_y");
            mSb.append(",");
            mSb.append("gyroscope_z");
        }


        if (mMagneticSensor != null) {
            mSb.append(",");
            mSb.append("magnetometer_x");
            mSb.append(",");
            mSb.append("magnetometer_y");
            mSb.append(",");
            mSb.append("magnetometer_z");
        }

        if (mOrientationSensor != null || isVirtualOrientationSensorSupport) {
            mSb.append(",");
            mSb.append("azimuth");
            mSb.append(",");
            mSb.append("pitch");
            mSb.append(",");
            mSb.append("roll");
        }


        DataToFileUtil.writeFileDataValue(mFileName, mSb.toString());
    }

    private void recordSensorDataToLogFile(CustomSensorInfo mSensor) {
        StringBuilder mStringBuilder = new StringBuilder();
        mStringBuilder.append(mSensor.user_id);
        mStringBuilder.append(",");

        mStringBuilder.append(mSensor.type);
        mStringBuilder.append(",");

        mStringBuilder.append(System.currentTimeMillis());

        if (mAccSensor != null) {
            mStringBuilder.append(",");
            mStringBuilder.append(mSensor.accelerometer_x);
            mStringBuilder.append(",");
            mStringBuilder.append(mSensor.accelerometer_y);
            mStringBuilder.append(",");
            mStringBuilder.append(mSensor.accelerometer_z);
        }

        if (mLineAccSensor != null) {
            mStringBuilder.append(",");
            mStringBuilder.append(mSensor.linear_acceleration_x);
            mStringBuilder.append(",");
            mStringBuilder.append(mSensor.linear_acceleration_y);
            mStringBuilder.append(",");
            mStringBuilder.append(mSensor.linear_acceleration_z);
        }

        if (mGyroSensor != null) {
            mStringBuilder.append(",");
            mStringBuilder.append(mSensor.gyroscope_x);
            mStringBuilder.append(",");
            mStringBuilder.append(mSensor.gyroscope_y);
            mStringBuilder.append(",");
            mStringBuilder.append(mSensor.gyroscope_z);
        }

        if (mMagneticSensor != null) {
            mStringBuilder.append(",");
            mStringBuilder.append(mSensor.magnetometer_x);
            mStringBuilder.append(",");
            mStringBuilder.append(mSensor.magnetometer_y);
            mStringBuilder.append(",");
            mStringBuilder.append(mSensor.magnetometer_z);
        }

        if (mOrientationSensor != null || isVirtualOrientationSensorSupport) {
            mStringBuilder.append(",");
            mStringBuilder.append(mSensor.azimuth);
            mStringBuilder.append(",");
            mStringBuilder.append(mSensor.pitch);
            mStringBuilder.append(",");
            mStringBuilder.append(mSensor.roll);
        }

        DataToFileUtil.writeFileDataValue(mFileName, mStringBuilder.toString());
    }
}
