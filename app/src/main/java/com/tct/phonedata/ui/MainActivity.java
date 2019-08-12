package com.tct.phonedata.ui;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.tct.phonedata.R;
import com.tct.phonedata.service.PhoneDataService;
import com.tct.phonedata.utils.DataToFileUtil;
import com.tct.phonedata.utils.MyConstant;
import com.tct.phonedata.utils.ShareUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public final static String KEY_SCENE_MODE = "key_scene";
    public final static String KEY_IS_TEST_RUNNING = "key_is_test_running";

    private static int REQUEST_PERMISSION_CODE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private final static int MSG_UPDATE_BTN = 0;

    private H mHandler = new H();
    private PhoneDataService.PhoneDataBinder mPhoneDataBinder;

    private TextView tv_info;
    private Button btn_start;
    private Spinner sp_spinner;

    private class H extends Handler {
        private H(){
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_BTN:
                    updateUI();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initValues();
        initListenser();
        bingMyService();
        initPermisson();
    }

    private void initViews() {
        sp_spinner = (Spinner)findViewById(R.id.sp_spinner);
        tv_info = (TextView) findViewById(R.id.tv_info);
        btn_start = (Button) findViewById(R.id.btn_start);
    }

    private void initValues() {
        StringBuilder mSb = new StringBuilder();
        mSb.append("功能说明:\n");
        mSb.append("1. 实时监听所有传感器数据，并保存到 SD card 中，用于导出分析\n");
        mSb.append("2. 数据结果存放位置如下:\n");
        mSb.append(DataToFileUtil.FILE_PATH);
        mSb.append("\n");

        tv_info.setText(mSb.toString());
    }

    private void initListenser() {
        btn_start.setOnClickListener(this);
    }

    private void initPermisson() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start:
                Log.d(MyConstant.TAG, "btn_start click");
                if(getString(R.string.btn_start).equals(btn_start.getText())) {
                    startServiceTest();

                    setTestRunningFlag(true);
                    mHandler.sendEmptyMessage(MSG_UPDATE_BTN);

                    int mPosition = sp_spinner.getSelectedItemPosition();
                    setSceneMode(mPosition);
                    Log.d(MyConstant.TAG, "Spinner position : " + mPosition);
                } else if(getString(R.string.btn_stop).equals(btn_start.getText())){
                    stopServiceTest();

                    setTestRunningFlag(false);
                    mHandler.sendEmptyMessage(MSG_UPDATE_BTN);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int mCount = 0;
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                Log.i(MyConstant.TAG, "Request Permissions:" + permissions[i] + ",Result:" + grantResults[i]);
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    mCount = mCount + 1;
                }
            }

            if (mCount != permissions.length) {
                Log.i(MyConstant.TAG, "not allow sdcard read and write, so finish self!");
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBingMyService();
    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPhoneDataBinder = (PhoneDataService.PhoneDataBinder) service;
            String mResultFilePath = mPhoneDataBinder.getResultFilePath();
            Log.d(MyConstant.TAG, "ResultFilePath :" + mResultFilePath);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private boolean isTestRunning(){
        ShareUtil mShareUtil = new ShareUtil(this);
        return mShareUtil.getBoolean(KEY_IS_TEST_RUNNING, false);
    }

    private void setTestRunningFlag(boolean value){
        ShareUtil mShareUtil = new ShareUtil(this);
        mShareUtil.setShare(KEY_IS_TEST_RUNNING, value);
    }


    private int getSceneMode() {
        ShareUtil mShareUtil = new ShareUtil(this);
        return mShareUtil.getInt(KEY_SCENE_MODE, 0);
    }

    private void setSceneMode(int value) {
        ShareUtil mShareUtil = new ShareUtil(this);
        mShareUtil.setShare(KEY_SCENE_MODE, value);
    }

    private void updateUI(){
        sp_spinner.setSelection(getSceneMode());

        if (isTestRunning()) {
            sp_spinner.setEnabled(false);
            btn_start.setText(getString(R.string.btn_stop));
        } else {
            sp_spinner.setEnabled(true);
            btn_start.setText(getString(R.string.btn_start));
        }
    }

    private void bingMyService(){
        Intent bindIntent = new Intent(this, PhoneDataService.class);
        bindService(bindIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void unBingMyService(){
        unbindService(mServiceConnection);
    }

    private void startServiceTest(){
        Log.d(MyConstant.TAG, "startServiceTest");
        Intent intent = new Intent();
        intent.setClass(this, PhoneDataService.class);
        intent.setAction(PhoneDataService.ACTION_START_TEST);
        startService(intent);
    }

    private void stopServiceTest(){
        Log.d(MyConstant.TAG, "stopServiceTest");

        Intent intent = new Intent();
        intent.setClass(this, PhoneDataService.class);
        intent.setAction(PhoneDataService.ACTION_STOP_TEST);
        startService(intent);
    }
}
