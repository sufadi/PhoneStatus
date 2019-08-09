package com.tct.phonedata;

import android.app.Application;
import android.content.Intent;

import com.tct.phonedata.service.PhoneDataService;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        startService();
    }

    private void startService(){
        Intent mIntent = new Intent();
        mIntent.setClass(this, PhoneDataService.class);
        startService(mIntent);
    }

}
