package com.iamverycute.air;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.suke.widget.SwitchButton;

import java.text.MessageFormat;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Shizuku.addRequestPermissionResultListener(this);
        SwitchButton switchButton = findViewById(R.id.switch_airplane_mode);
        switchButton.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
        if (permissionIsGranted) {
            int errorCount = ShizukuConnectivityManager.setAirplaneMode(view.isChecked());
            if (errorCount != 0) {
                Toast.makeText(this, MessageFormat.format("Report error count: {0}", errorCount), Toast.LENGTH_SHORT).show();
            }
        }
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
                Shizuku.requestPermission(0);
            }
        }
        super.onResume();
    }
}