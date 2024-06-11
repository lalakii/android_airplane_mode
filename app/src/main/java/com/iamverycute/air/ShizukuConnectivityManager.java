package com.iamverycute.air;

import android.content.Context;
import android.os.IBinder;

import java.lang.reflect.Method;

import rikka.shizuku.ShizukuBinderWrapper;
import rikka.shizuku.SystemServiceHelper;

/***
 * setAirplaneMode invoke
 * @author lalaki.cn (i@lalaki.cn)
 */
@SuppressWarnings({"PrivateApi", "unused"})
class ShizukuConnectivityManager {
    private static Object miConnectivityManager;
    private static Method mSetAirplaneMode;

    static {
        try {
            miConnectivityManager = Class.forName("android.net.IConnectivityManager$Stub").getMethod("asInterface", IBinder.class).invoke(null, new ShizukuBinderWrapper(SystemServiceHelper.getSystemService(Context.CONNECTIVITY_SERVICE)));
            Object iConnectivityManager = miConnectivityManager;
            if (iConnectivityManager != null) {
                mSetAirplaneMode = iConnectivityManager.getClass().getDeclaredMethod("setAirplaneMode", boolean.class);
            }
        } catch (Throwable ignored) {
        }
    }

    /***
     * setAirplaneMode
     * @param enable isEnabled
     * @return errorCount
     */
    public static int setAirplaneMode(boolean enable) {
        int errorCount = 0;
        Object iConnectivityManager = miConnectivityManager;
        Method setAirplaneMode = mSetAirplaneMode;
        if (iConnectivityManager != null && setAirplaneMode != null) {
            try {
                setAirplaneMode.invoke(iConnectivityManager, enable);
            } catch (Throwable ignored) {
                ++errorCount;
            }
        } else {
            ++errorCount;
        }
        return errorCount;
    }
}