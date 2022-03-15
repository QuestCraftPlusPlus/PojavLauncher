package net.kdt.pojavlaunch.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

public class UiUitls extends Activity {

    public static Handler UIHandler;

    static {
        UIHandler = new Handler(Looper.getMainLooper());
    }

    public static void runOnUI(Runnable runnable) {
        UIHandler.post(runnable);
    }
}