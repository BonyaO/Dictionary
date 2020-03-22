package com.bonya.diction.threading;

import android.os.Handler;
import android.os.Looper;

public class MyThread extends Thread {
    private static final String TAG = "MyThread";
    private Handler mMyHandler = null;

    @Override
    public void run() {
        Looper.prepare();
        mMyHandler = new Handler(Looper.myLooper());
        Looper.loop();
    }

    private  class MyThreadHandler
}
