package com.project.app.permissions.manager.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorRes;

public class Tools {

    public static final String SHARED_PREFERENCE_KEY = "app_preferences";
    public static final String KEY_FIRST_OPEN = "first_open";

    public static final String KEY_SYSTEM_APPS_COUNT = "system_apps_count";
    public static final String KEY_PLAY_STORE_APPS_COUNT = "play_store_apps_count";
    public static final String KEY_ADB_APPS_COUNT = "adb_apps_count";
    public static final String KEY_PKG_INSTALLER_APPS_COUNT = "pkg_installer_apps_count";

    public static final String KEY_CAMERA_PERMS_APPS_COUNT = "camera_apps_count";
    public static final String KEY_MICROPHONE_PERMS_APPS_COUNT = "microphone_apps_count";
    public static final String KEY_LOCATION_PERMS_APPS_COUNT = "location_apps_count";

    public static final String KEY_LAST_SCAN_TIME = "last_scan_time";


    public static void setSystemBarColor(Activity act, @ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = act.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(act.getResources().getColor(color));
        }
    }

    public static Drawable getAppIcon(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
            return appInfo.loadIcon(packageManager);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
