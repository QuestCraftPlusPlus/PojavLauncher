package net.kdt.pojavlaunch;

import android.app.Activity;

public class MCXRLoader {
    static {
        System.loadLibrary("openvr_api");
    }

    public static native void launch(MainActivity activity);
}
