package com.example.googlessmsretrieverapi;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        AppSignature appSignature = new AppSignature(this);
        appSignature.getAppSignatures();
        mInstance = this;
    }

    public static MyApplication getInstance() {
        return mInstance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

}
