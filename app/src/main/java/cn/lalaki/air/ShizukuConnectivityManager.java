package cn.lalaki.air;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;

import androidx.annotation.WorkerThread;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import rikka.shizuku.Shizuku;
import rikka.shizuku.ShizukuBinderWrapper;
import rikka.shizuku.SystemServiceHelper;

/***
 * setAirplaneMode invoke
 * @author lalaki.cn (i@lalaki.cn)
 */
@SuppressWarnings({"PrivateApi", "unused"})
public class ShizukuConnectivityManager implements ServiceConnection {
    private static Object miConnectivityManager;
    private static Method mSetAirplaneMode;
    private IShellExecuteService mIShellExecuteService;
    private Shizuku.UserServiceArgs mUserServiceArgs;
    private CountDownLatch mCountDownLatch;
    private boolean mEnabled;

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
     * isAirplaneMode
     * @param context Activity
     * @return enum AirplaneMode
     */
    public static AirplaneMode isAirplaneMode(Context context) {
        int ret = Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, -1);
        return switch (ret) {
            case 0 -> AirplaneMode.Off;
            case 1 -> AirplaneMode.On;
            default -> AirplaneMode.Unknown;
        };
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

    /***
     * setAirplaneModeWithShell
     * @param context Activity
     * @param enable isEnabled
     */
    @WorkerThread
    public void setAirplaneModeWithShell(Context context, boolean enable) {
        IShellExecuteService shellExecuteService = mIShellExecuteService;
        if (shellExecuteService != null) {
            try {
                shellExecuteService.setAirplaneModeWithShell(enable);
            } catch (RemoteException ignored) {
            }
        } else {
            Shizuku.UserServiceArgs userServiceArgs = mUserServiceArgs;
            if (userServiceArgs == null) {
                userServiceArgs = new Shizuku.UserServiceArgs(new ComponentName(context.getPackageName(), ShellExecuteService.class.getName())).daemon(false).debuggable(false).processNameSuffix("airplane_mode_by_lalaki").version(1);
                mUserServiceArgs = userServiceArgs;
            }
            mEnabled = enable;
            CountDownLatch countDownLatch = mCountDownLatch;
            if (countDownLatch == null) {
                countDownLatch = new CountDownLatch(1);
                mCountDownLatch = countDownLatch;
            }
            Shizuku.bindUserService(userServiceArgs, this);
            try {
                boolean ignored = countDownLatch.await(30, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void unbindService(boolean remove) {
        Shizuku.UserServiceArgs userServiceArgs = mUserServiceArgs;
        if (userServiceArgs != null) {
            Shizuku.unbindUserService(userServiceArgs, this, remove);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        if (service != null && service.pingBinder()) {
            mIShellExecuteService = IShellExecuteService.Stub.asInterface(service);
            boolean enable = mEnabled;
            setAirplaneMode(enable);
        }
        CountDownLatch countDownLatch = mCountDownLatch;
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mIShellExecuteService = null;
    }

    public enum AirplaneMode {
        On, Off, Unknown
    }
}