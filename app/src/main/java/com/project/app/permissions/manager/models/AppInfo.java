package com.project.app.permissions.manager.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class AppInfo implements Parcelable {
    String appName;
    String packageName;
    boolean isSystemApp;
    String sourceOfInstallation;
    ArrayList<String> permissions = new ArrayList<>();

    public AppInfo() {
    }

    protected AppInfo(Parcel in) {
        appName = in.readString();
        packageName = in.readString();
        isSystemApp = in.readByte() != 0;
        sourceOfInstallation = in.readString();
        permissions = in.createStringArrayList();
    }

    public static final Creator<AppInfo> CREATOR = new Creator<AppInfo>() {
        @Override
        public AppInfo createFromParcel(Parcel in) {
            return new AppInfo(in);
        }

        @Override
        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(appName);
        dest.writeString(packageName);
        dest.writeByte((byte) (isSystemApp ? 1 : 0));
        dest.writeString(sourceOfInstallation);
        dest.writeStringList(permissions);
    }
}
