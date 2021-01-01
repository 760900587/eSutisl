package com.example.esutisl;

import android.app.Application;

public class MyApp extends Application {

    public static Mysql mysql;

    @Override
    public void onCreate() {
        super.onCreate();

        mysql = new Mysql(this);
    }
}
