package com.project.app.permissions.manager.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.app.permissions.manager.R;
import com.project.app.permissions.manager.models.AppInfo;
import com.project.app.permissions.manager.utils.Tools;

import java.util.ArrayList;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<AppInfo> appList;
    private PackageManager packageManager;

    public AppListAdapter(Context context, ArrayList<AppInfo> appList) {
        this.context = context;
        this.appList = appList;
        this.packageManager = context.getPackageManager();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_app, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppInfo app = appList.get(position);

        holder.appNameTextView.setText(app.getAppName());
        holder.packageNameTextView.setText(app.getPackageName());
        holder.dangerousPermissionsTextView.setText("Dangerous Permissions: " + app.getPermissions().size());

        holder.appIconImageView.setImageDrawable(Tools.getAppIcon(context, app.getPackageName()));

        holder.deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Uninstall the app
                Uri packageUri = Uri.parse("package:" + app.getPackageName());
                Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
                context.startActivity(uninstallIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIconImageView;
        TextView appNameTextView;
        TextView packageNameTextView;
        TextView dangerousPermissionsTextView;
        ImageButton deleteImageButton;

        public ViewHolder(View itemView) {
            super(itemView);
            appIconImageView = itemView.findViewById(R.id.appIconImageView);
            appNameTextView = itemView.findViewById(R.id.appNameTextView);
            packageNameTextView = itemView.findViewById(R.id.packageNameTextView);
            dangerousPermissionsTextView = itemView.findViewById(R.id.dangerousPermissionsTextView);
            deleteImageButton = itemView.findViewById(R.id.deleteImageButton);
        }
    }

    public void setAppList(ArrayList<AppInfo> appList) {
        this.appList = appList;
        notifyDataSetChanged();
    }
}
