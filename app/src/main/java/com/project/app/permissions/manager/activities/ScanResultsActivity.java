package com.project.app.permissions.manager.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.project.app.permissions.manager.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.project.app.permissions.manager.adapters.AppListAdapter;
import com.project.app.permissions.manager.models.AppInfo;
import com.project.app.permissions.manager.utils.Tools;

import java.util.ArrayList;

public class ScanResultsActivity extends AppCompatActivity implements AppListAdapter.OnItemClickListener{

    private RecyclerView recyclerView;
    private AppListAdapter appListAdapter;

    private ImageView ivNoApps;
    private TextView tvNoApps;

    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;

    private static final int APP_UNINSTALL_REQUEST_CODE = 123;


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

        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);

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
            appListAdapter = new AppListAdapter(this, targetList, this);
            recyclerView.setAdapter(appListAdapter);
            tvNoApps.setVisibility(View.GONE);
            ivNoApps.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onItemClick(AppInfo appInfo) {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.sheet_bottom_uninstall, null);

        ((TextView) view.findViewById(R.id.sheet_app_name)).setText(appInfo.getAppName());
        ((TextView) view.findViewById(R.id.sheet_app_package)).setText(appInfo.getPackageName());
        ((ImageView) view.findViewById(R.id.sheet_app_icon)).setImageDrawable(Tools.getAppIcon(this, appInfo.getPackageName()));
        (view.findViewById(R.id.sheet_btn_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.hide();
            }
        });

        Button btnUninstall = view.findViewById(R.id.sheet_app_uninstall_btn);

        if (appInfo.isSystemApp()){
            btnUninstall.setEnabled(false);
            btnUninstall.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.grey_60)));
            btnUninstall.setText("System Apps can't be uninstalled");
        }


        btnUninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                intent.setData(Uri.parse("package:" + appInfo.getPackageName()));
                intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                startActivityForResult(intent, APP_UNINSTALL_REQUEST_CODE);
            }
        });
        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        // set background transparent
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_UNINSTALL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "App uninstalled", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("TAG", "onActivityResult: user canceled the (un)install");
            } else if (resultCode == RESULT_FIRST_USER) {
                //Log.d("TAG", "onActivityResult: failed to (un)install");
                Toast.makeText(this, "Uninstallation failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}