package com.tct.phonedata;

import android.app.Application;
import android.content.Intent;

import com.tct.phonedata.service.PhoneDataService;
import com.tct.phonedata.ui.MainActivity;
import com.tct.phonedata.utils.ShareUtil;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        startService();

        initRunnintTestFlag();
        initSceneModeFlag();
    }

    private void startService(){
        Intent mIntent = new Intent();
        mIntent.setClass(this, PhoneDataService.class);
        startService(mIntent);
    }

    private void initRunnintTestFlag(){
        ShareUtil mShareUtil = new ShareUtil(this);
        mShareUtil.setShare(MainActivity.KEY_IS_TEST_RUNNING, false);
    }

    private void initSceneModeFlag(){
        ShareUtil mShareUtil = new ShareUtil(this);
        mShareUtil.setShare(MainActivity.KEY_SCENE_MODE, 0);
    }

}
