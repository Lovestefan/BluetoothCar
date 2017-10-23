package com.bolaa.bluetoothcar.db;

import android.app.Application;

/**
 * 作者：Administrator on 2017/10/16 10:33
 * 邮箱：xiaobo.pu@bolaa.com
 * 手机:15223197346
 */

public class DemoApplication extends Application
{
    private static DemoApplication demoApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        demoApplication = this;
    }
    public static DemoApplication getInstance() {
        return demoApplication;
    }
}
