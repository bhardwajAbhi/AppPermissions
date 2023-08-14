package com.project.app.permissions.manager.models;

import java.util.ArrayList;

public class AppInfo {
    String appName;
    String packageName;
    boolean isSystemApp;
    String sourceOfInstallation;
    ArrayList<String> permissions = new ArrayList<>();

    public AppInfo() {
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isSystemApp() {
        return isSystemApp;
    }

    public void setSystemApp(boolean systemApp) {
        isSystemApp = systemApp;
    }

    public String getSourceOfInstallation() {
        return sourceOfInstallation;
    }

    public void setSourceOfInstallation(String sourceOfInstallation) {
        this.sourceOfInstallation = sourceOfInstallation;
    }

    public ArrayList<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(ArrayList<String> permissions) {
        this.permissions = permissions;
    }
}
