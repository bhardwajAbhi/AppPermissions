package com.project.app.permissions.manager.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.app.permissions.manager.R;
import com.project.app.permissions.manager.models.AppInfo;
import com.project.app.permissions.manager.utils.Tools;

public class AppDetailsActivity extends AppCompatActivity {

    private AppInfo appInfo;
    private PackageInfo packageInfo;

    private ImageView ivAppIcon, ivInstallerIcon;
    private TextView tvAppName, tvPackageName, tvVersionCode, tvVersionName, tvInstallerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_details);
        appInfo = getIntent().getParcelableExtra(Tools.KEY_APP_DETAILS_PARCELABLE);
        packageInfo = Tools.getPackageInformation(this, appInfo.getPackageName());
        initViews();
        populateDetails();
    }

    private void initViews() {
        ivAppIcon = findViewById(R.id.app_details_iv_app_icon);
        ivInstallerIcon = findViewById(R.id.app_details_iv_app_installer_icon);

        tvAppName = findViewById(R.id.app_details_tv_app_name);
        tvPackageName = findViewById(R.id.app_details_tv_app_package_name);

        tvVersionCode = findViewById(R.id.app_details_tv_version_code);
        tvVersionName = findViewById(R.id.app_details_tv_version_name);
        tvInstallerName = findViewById(R.id.app_details_tv_installed_from);
    }

    private void populateDetails() {
        ivAppIcon.setImageDrawable(Tools.getAppIcon(this, appInfo.getPackageName()));
        tvAppName.setText(appInfo.getAppName());
        tvPackageName.setText(appInfo.getPackageName());

        tvInstallerName.setText(appInfo.getSourceOfInstallation());
        tvVersionName.setText(packageInfo.versionName);
        tvVersionCode.setText(packageInfo.getLongVersionCode()+"");
    }


}