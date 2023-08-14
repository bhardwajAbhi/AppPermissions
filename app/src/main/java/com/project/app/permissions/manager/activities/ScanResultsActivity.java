package com.project.app.permissions.manager.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.app.permissions.manager.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.project.app.permissions.manager.adapters.AppListAdapter;
import com.project.app.permissions.manager.models.AppInfo;
import com.project.app.permissions.manager.utils.Tools;

import java.util.ArrayList;

public class ScanResultsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppListAdapter appListAdapter;

    private ImageView ivNoApps;
    private TextView tvNoApps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_results);
        Tools.setSystemBarColor(this, R.color.colorPrimary);
        recyclerView = findViewById(R.id.scan_results_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        ivNoApps = findViewById(R.id.iv_no_apps_found);
        tvNoApps = findViewById(R.id.tv_no_apps_found);

        loadAppListForCategory("system"); //initial loading

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        int b = item.getItemId();
                        if (b == R.id.navigation_btn_system) {
                            loadAppListForCategory("system");
                        } else if (b == R.id.navigation_btn_playstore) {
                            loadAppListForCategory("play_store");
                        } else if (b == R.id.navigation_btn_adb) {
                            loadAppListForCategory("adb");
                        } else if (b == R.id.navigation_btn_pkginstaller) {
                            loadAppListForCategory("pkg_installer");
                        }
                        return true;
                    }
                });
    }

    private void loadAppListForCategory(String category) {
        ArrayList<AppInfo> targetList = null;

        if (category.equals("system")) {
            targetList = HomeActivity.systemApps;
        } else if (category.equals("play_store")) {
            targetList = HomeActivity.playStoreApps;
        } else if (category.equals("adb")) {
            targetList = HomeActivity.adbApps;
        } else if (category.equals("pkg_installer")) {
            targetList = HomeActivity.pkgInstallerApps;
        }

        if (targetList.isEmpty()){
            tvNoApps.setText("No " + category.toUpperCase().replace("_", " ") + " apps Found");
            tvNoApps.setVisibility(View.VISIBLE);
            ivNoApps.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            appListAdapter = new AppListAdapter(this, targetList);
            recyclerView.setAdapter(appListAdapter);
            tvNoApps.setVisibility(View.GONE);
            ivNoApps.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

    }

}