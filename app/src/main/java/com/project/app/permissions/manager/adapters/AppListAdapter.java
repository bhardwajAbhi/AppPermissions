package com.project.app.permissions.manager.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.app.permissions.manager.R;
import com.project.app.permissions.manager.activities.AppDetailsActivity;
import com.project.app.permissions.manager.models.AppInfo;
import com.project.app.permissions.manager.utils.Tools;

import java.util.ArrayList;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<AppInfo> appList;
    private PackageManager packageManager;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(AppInfo appInfo);
    }

    public AppListAdapter(Context context, ArrayList<AppInfo> appList, OnItemClickListener listener) {
        this.context = context;
        this.appList = appList;
        this.packageManager = context.getPackageManager();
        this.listener = listener;
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
        holder.dangerousPermissionsTextView.setText("Permissions: " + app.getPermissions().size());

        holder.appIconImageView.setImageDrawable(Tools.getAppIcon(context, app.getPackageName()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openDetailsActivity = new Intent(context, AppDetailsActivity.class);
                openDetailsActivity.putExtra(Tools.KEY_APP_DETAILS_PARCELABLE, app);
                context.startActivity(openDetailsActivity);
            }
        });

        holder.deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(app);
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
