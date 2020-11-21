package com.elexlab.mydisk;

import android.app.Application;

import com.elexlab.mydisk.datasource.HeroLib;

public class MyDiskApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HeroLib.getInstance().appContext = this;
    }
}
