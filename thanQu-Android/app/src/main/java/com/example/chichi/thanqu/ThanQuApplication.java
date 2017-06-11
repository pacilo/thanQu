package com.example.chichi.thanqu;

import android.app.Application;

import java.net.CookieHandler;
import java.net.CookieManager;

/**
 * Created by pacilo on 2017. 6. 7..
 */

public class ThanQuApplication extends Application {

    static CookieManager cookieManage = new CookieManager();

    @Override
    public void onCreate() {
        super.onCreate();

        CookieHandler.setDefault(cookieManage);
    }
}
