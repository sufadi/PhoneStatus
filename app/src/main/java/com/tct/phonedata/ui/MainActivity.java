package com.tct.phonedata.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.tct.phonedata.R;
import com.tct.phonedata.service.PhoneDataService;
import com.tct.phonedata.utils.MyConstant;
import com.tct.phonedata.utils.ShareUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public final static String KEY_IS_TEST_RUNNING = "key_is_test_running";

    private final static int MSG_UPDATE_BTN = 0;

    private H mHandler = new H();
    private PhoneDataService.PhoneDataBinder mPhoneDataBinder;

    private Button btn_start;


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
    }

    private void initViews() {
        btn_start = (Button) findViewById(R.id.btn_start);
    }

    private void initValues() {
    }

    private void initListenser() {
        btn_start.setOnClickListener(this);
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
                } else if(getString(R.string.btn_stop).equals(btn_start.getText())){
                    stopServiceTest();

                    setTestRunningFlag(false);
                    mHandler.sendEmptyMessage(MSG_UPDATE_BTN);
                }
                break;
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

    private void updateUI(){
        btn_start.setText(isTestRunning() ? getString(R.string.btn_stop) : getString(R.string.btn_start));
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
