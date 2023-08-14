package com.project.app.permissions.manager.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.project.app.permissions.manager.R;
import com.project.app.permissions.manager.models.AppInfo;
import com.project.app.permissions.manager.utils.Tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    enum ButtonState {
        NORMAL, LOADING, DONE
    }

    private ProgressBar progress_indeterminate_circular;
    private View button_action;
    private TextView tv_status, tv_download;
    private ImageView icon_download;
    private CardView card_view;
    private ButtonState buttonState = ButtonState.NORMAL;

    public static ArrayList<AppInfo> systemApps;
    public static ArrayList<AppInfo> thirdPartyApps;

    public static ArrayList<AppInfo> playStoreApps;

    public static ArrayList<AppInfo> adbApps;

    public static ArrayList<AppInfo> pkgInstallerApps;

    public static ArrayList<AppInfo> cameraApps;
    public static ArrayList<AppInfo> locationApps;
    public static ArrayList<AppInfo> microphoneApps;

    private String curDateTime;


    // UI Widgets
    private TextView tvSystemAppsCount, tvPlayStoreAppsCount, tvAdbAppsCount, tvPkgInstallerAppsCount,
            tvCameraAppsCount, tvLocationAppsCount, tvMicrophoneAppsCount, tvLastScanTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initToolbar();
        initCountViews();
        initScanButtonView();

    }

    private void initCountViews() {
        tvSystemAppsCount = findViewById(R.id.tv_system_apps_count);
        tvPlayStoreAppsCount = findViewById(R.id.tv_playstore_apps_count);
        tvAdbAppsCount = findViewById(R.id.tv_adb_apps_count);
        tvPkgInstallerAppsCount = findViewById(R.id.tv_pkg_installer_apps_count);

        tvCameraAppsCount = findViewById(R.id.tv_camera_permissions_apps);
        tvMicrophoneAppsCount = findViewById(R.id.tv_audio_permissions_apps);
        tvLocationAppsCount = findViewById(R.id.tv_location_permissions_apps);

        tvLastScanTime = findViewById(R.id.tv_last_scan_time);

        populateAppCountInViews();
    }

    private void populateAppCountInViews() {
        SharedPreferences mySharedPrefs = getSharedPreferences(Tools.SHARED_PREFERENCE_KEY, MODE_PRIVATE);

        tvSystemAppsCount.setText(String.valueOf(mySharedPrefs.getInt(Tools.KEY_SYSTEM_APPS_COUNT, 0)));
        tvPlayStoreAppsCount.setText(String.valueOf(mySharedPrefs.getInt(Tools.KEY_PLAY_STORE_APPS_COUNT, 0)));
        tvAdbAppsCount.setText(String.valueOf(mySharedPrefs.getInt(Tools.KEY_ADB_APPS_COUNT, 0)));
        tvPkgInstallerAppsCount.setText(String.valueOf(mySharedPrefs.getInt(Tools.KEY_PKG_INSTALLER_APPS_COUNT, 0)));

        tvCameraAppsCount.setText(getAppsCount(mySharedPrefs.getInt(Tools.KEY_CAMERA_PERMS_APPS_COUNT, 0)));
        tvMicrophoneAppsCount.setText(getAppsCount(mySharedPrefs.getInt(Tools.KEY_MICROPHONE_PERMS_APPS_COUNT, 0)));
        tvLocationAppsCount.setText(getAppsCount(mySharedPrefs.getInt(Tools.KEY_LOCATION_PERMS_APPS_COUNT, 0)));

        tvLastScanTime.setText(mySharedPrefs.getString(Tools.KEY_LAST_SCAN_TIME, "Never"));
    }

    private void initScanButtonView() {
        progress_indeterminate_circular = findViewById(R.id.progress_indeterminate_circular);
        button_action = findViewById(R.id.button_action);
        tv_status = findViewById(R.id.tv_status);
        tv_download = findViewById(R.id.tv_download);
        icon_download = findViewById(R.id.icon_download);
        card_view = findViewById(R.id.card_view);

        button_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonState == ButtonState.DONE) {
                    /* i.e. one scan has been completed already
                     *  move the user to results screen */
                    startActivity(new Intent(HomeActivity.this, ScanResultsActivity.class));
                } else {
                    /* i.e. no scan has been performed yet.
                     *  so init the scan process */
                    new LoadAppsTask().execute();
                }
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.home_activity_toolbar);
        setSupportActionBar(toolbar);
        Tools.setSystemBarColor(this, R.color.colorPrimary);
    }


    public void onResetClicked(View view) {
        button_action.setClickable(true);
        buttonState = buttonState.NORMAL;
        progress_indeterminate_circular.setProgress(0);
        progress_indeterminate_circular.setVisibility(View.INVISIBLE);
        icon_download.setImageResource(R.drawable.ic_process);
        icon_download.setVisibility(View.VISIBLE);
        tv_status.setText("");
        tv_download.setText("Scan Apps");
        card_view.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


    // ------------------------------------------------------------------ Core Async Task for loading the details of an application -------------------------------------
    private class LoadAppsTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            systemApps = new ArrayList<>();
            thirdPartyApps = new ArrayList<>();
            playStoreApps = new ArrayList<>();
            adbApps = new ArrayList<>();
            pkgInstallerApps = new ArrayList<>();
            cameraApps = new ArrayList<>();
            locationApps = new ArrayList<>();
            microphoneApps = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... params) {

            PackageManager packageManager = getPackageManager();
            List<ApplicationInfo> appList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            int totalApps = appList.size();
            int progress = 0;


            for (ApplicationInfo appInfo : appList) {
                AppInfo app = new AppInfo();
                app.setAppName(appInfo.loadLabel(packageManager).toString());
                app.setPackageName(appInfo.packageName);
                app.setSystemApp((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1);

                // Set the source of installation
                setInstallationSource(packageManager, appInfo, app);

                // Get the set of permissions
                try {
                    String[] requestedPermissions = packageManager.getPackageInfo(app.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions;
                    if (requestedPermissions != null) {
                        for (String permission : requestedPermissions) {
                            app.getPermissions().add(permission);

                            // Check for camera, location, and microphone permissions
                            if (permission.startsWith("android.permission.CAMERA")) {
                                cameraApps.add(app);
                            } else if (permission.startsWith("android.permission.ACCESS_FINE_LOCATION") ||
                                    permission.startsWith("android.permission.ACCESS_COARSE_LOCATION")) {
                                locationApps.add(app);
                            } else if (permission.startsWith("android.permission.RECORD_AUDIO")) {
                                microphoneApps.add(app);
                            }
                        }
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                if (app.isSystemApp()) {
                    systemApps.add(app);
                } else {
                    thirdPartyApps.add(app);
                }

                if (app.getSourceOfInstallation().equalsIgnoreCase("Play Store")){
                    playStoreApps.add(app);
                }

                if (app.getSourceOfInstallation().equalsIgnoreCase("Default Package Installer")){
                    pkgInstallerApps.add(app);
                }

                if (app.getSourceOfInstallation().equalsIgnoreCase("ADB")){
                    adbApps.add(app);
                }


                progress++;
                publishProgress((progress * 100) / totalApps);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            buttonState = buttonState.LOADING;
            int progress = values[0];
            progress_indeterminate_circular.setProgress(progress);
            tv_status.setText(progress + " %");
            tv_download.setText("Scanning Applications...");
            button_action.setClickable(false);
            progress_indeterminate_circular.setVisibility(View.VISIBLE);
            icon_download.setVisibility(View.INVISIBLE);
            card_view.setCardBackgroundColor(getResources().getColor(R.color.orange_400));
        }

        @Override
        protected void onPostExecute(Void unused) {

            //update the state of progress bar first
            buttonState = buttonState.DONE;
            progress_indeterminate_circular.setProgress(0);
            tv_status.setText("DONE");
            tv_download.setText("View Results");
            button_action.setClickable(true);
            progress_indeterminate_circular.setVisibility(View.INVISIBLE);
            icon_download.setVisibility(View.VISIBLE);
            icon_download.setImageResource(R.drawable.ic_download_done);
            card_view.setCardBackgroundColor(getResources().getColor(R.color.light_green_500));
            //startActivity(new Intent(HomeActivity.this, ScanResultsActivity.class));


            //Update the counts in the respective UI widgets
            tvSystemAppsCount.setText(String.valueOf(systemApps.size()));
            tvPlayStoreAppsCount.setText(String.valueOf(playStoreApps.size()));
            tvAdbAppsCount.setText(String.valueOf(adbApps.size()));
            tvPkgInstallerAppsCount.setText(String.valueOf(pkgInstallerApps.size()));

            tvCameraAppsCount.setText(getAppsCount(cameraApps.size()));
            tvMicrophoneAppsCount.setText(getAppsCount(microphoneApps.size()));
            tvLocationAppsCount.setText(getAppsCount(locationApps.size()));

            curDateTime = java.text.DateFormat.getDateTimeInstance().format(new Date());
            tvLastScanTime.setText(curDateTime);
            
            saveCountValuesInSharedPreferences();
        }



        private void setInstallationSource(PackageManager packageManager, ApplicationInfo appInfo, AppInfo app) {
            String appInstallerName = packageManager.getInstallerPackageName(appInfo.packageName);
            if (appInstallerName != null) {
                if (appInstallerName.equals("com.android.vending")) {
                    app.setSourceOfInstallation("Play Store");
                } else if (appInstallerName.equals("com.google.android.packageinstaller")) {
                    app.setSourceOfInstallation("Default Package Installer");
                } else {
                    app.setSourceOfInstallation("Unknown Source");
                }
            } else if ((appInstallerName == null || appInstallerName.equals("pc")) && !app.isSystemApp()) {
                app.setSourceOfInstallation("ADB");
            } else if ((appInstallerName == null) && app.isSystemApp()) {
                app.setSourceOfInstallation("System");
            } else {
                app.setSourceOfInstallation("Unknown Source");
            }
        }
    }

    private void saveCountValuesInSharedPreferences() {
        SharedPreferences mySharedPrefs = getSharedPreferences(Tools.SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor myEditor = mySharedPrefs.edit();

        myEditor.putInt(Tools.KEY_SYSTEM_APPS_COUNT, systemApps.size());
        myEditor.putInt(Tools.KEY_PLAY_STORE_APPS_COUNT, playStoreApps.size());
        myEditor.putInt(Tools.KEY_ADB_APPS_COUNT, adbApps.size());
        myEditor.putInt(Tools.KEY_PKG_INSTALLER_APPS_COUNT, pkgInstallerApps.size());

        myEditor.putInt(Tools.KEY_CAMERA_PERMS_APPS_COUNT, cameraApps.size());
        myEditor.putInt(Tools.KEY_LOCATION_PERMS_APPS_COUNT, locationApps.size());
        myEditor.putInt(Tools.KEY_MICROPHONE_PERMS_APPS_COUNT, microphoneApps.size());

        myEditor.putString(Tools.KEY_LAST_SCAN_TIME, curDateTime);
        myEditor.commit();
    }

    private String getAppsCount(int size) {
        if (size == 1){
            return size + " App";
        }
        return size + " Apps";
    }

}