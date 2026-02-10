package cn.lalaki.air.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import cn.lalaki.air.R;
import cn.lalaki.air.ShizukuConnectivityManager;

import com.suke.widget.SwitchButton;

import rikka.shizuku.Shizuku;
import rikka.shizuku.ShizukuProvider;

/**
 * Created on yyyy-MM-dd
 *
 * <p>测试类
 *
 * @author lalaki (i@lalaki.cn)
 * @since android airplane mode <a href="https://github.com/lalakii/android_airplane_mode/">android airplane mode</a>
 */
public class AirActivity extends Activity implements SwitchButton.OnCheckedChangeListener, Shizuku.OnRequestPermissionResultListener {
    private boolean permissionIsGranted = false;
    private ShizukuConnectivityManager mShizukuConnectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Shizuku.addRequestPermissionResultListener(this);
        SwitchButton switchAirplaneModeButton = findViewById(R.id.switch_airplane_mode);
        switchAirplaneModeButton.setOnCheckedChangeListener(this);
        SwitchButton switchAirplaneModeWithShellButton = findViewById(R.id.switch_airplane_mode_with_shell);
        switchAirplaneModeWithShellButton.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
        if (permissionIsGranted) {
            int id = view.getId();
            if (id == R.id.switch_airplane_mode) {
                // method1 hidden api, required targetSdk 27
                int errorCount = ShizukuConnectivityManager.setAirplaneMode(isChecked);
                if (errorCount == 0) {
                    detectAirplaneMode();
                } else {
                    Toast.makeText(this, R.string.unsupported, Toast.LENGTH_SHORT).show();
                }
            } else if (id == R.id.switch_airplane_mode_with_shell) {
                // method2 adb shell (shizuku)
                setAirplaneModeWithShellAsync(isChecked, this::detectAirplaneMode);
            }
        }
    }

    /***
     * detectAirplaneMode
     */
    private void detectAirplaneMode() {
        ShizukuConnectivityManager.AirplaneMode airplaneModeResult = ShizukuConnectivityManager.isAirplaneMode(this);
        int msgId = switch (airplaneModeResult) {
            case On -> R.string.air_on;
            case Off -> R.string.air_off;
            default -> R.string.air_unknown;
        };
        Toast.makeText(this, msgId, Toast.LENGTH_SHORT).show();
    }

    private void setAirplaneModeWithShellAsync(boolean enable, Runnable callback) {
        // required thread
        new Thread(() -> {
            ShizukuConnectivityManager shizukuConnectivityManager = mShizukuConnectivityManager;
            if (shizukuConnectivityManager == null) {
                shizukuConnectivityManager = new ShizukuConnectivityManager();
                mShizukuConnectivityManager = shizukuConnectivityManager;
            }
            shizukuConnectivityManager.setAirplaneModeWithShell(this, enable);
            if (callback != null) {
                runOnUiThread(callback);
            }
        }).start();
    }

    @Override
    public void onRequestPermissionResult(int requestCode, int grantResult) {
        permissionIsGranted = grantResult == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onResume() {
        Intent shizukuIntent = getPackageManager().getLaunchIntentForPackage(ShizukuProvider.MANAGER_APPLICATION_ID);
        if (shizukuIntent != null && Shizuku.pingBinder()) {
            permissionIsGranted = Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
            if (permissionIsGranted) {
                findViewById(R.id.tips).setVisibility(View.GONE);
            } else {
                Shizuku.requestPermission(0x2026);
            }
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        ShizukuConnectivityManager scm = mShizukuConnectivityManager;
        if (scm != null) {
            scm.unbindService(true);
        }
        super.onDestroy();
    }
}