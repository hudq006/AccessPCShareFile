package com.hudq.visitor.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hudq.visitor.R;
import com.hudq.visitor.util.FormatUtil;

import java.util.List;

import jcifs.smb.SmbFile;

/**
 * 目录adapter
 * <p>
 * Created by hudq on 2016/12/15.
 */

public class DirectoryAdapter extends BaseAdapter {

    private static final String TAG = "DirectoryAdapter";
    private Context mContext;
    private List<SmbFile> files;

    Drawable folderImg, fileImg;

    public DirectoryAdapter(Context context, List<SmbFile> files) {
        this.mContext = context;
        this.files = files;

        folderImg = mContext.getResources().getDrawable(R.mipmap.format_folder);
        fileImg = mContext.getResources().getDrawable(R.mipmap.format_file);
    }

    @Override
    public int getCount() {
        return files != null ? files.size() : 0;
    }

    @Override
    public SmbFile getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_list_direcory, null);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.item_dire_icon);
            holder.name = (TextView) convertView.findViewById(R.id.item_dire_name);
            holder.info = (TextView) convertView.findViewById(R.id.item_dire_info);
            holder.time = (TextView) convertView.findViewById(R.id.item_dire_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            SmbFile file = files.get(position);
            printInfo(file);
            if (file.isDirectory()) {
                holder.icon.setImageDrawable(folderImg);
                holder.info.setText("文件夹");
            } else if (file.isFile()) {
                holder.icon.setImageDrawable(fileImg);
                holder.info.setText(FormatUtil.formatSize(file.length()));
            } else {
                holder.icon.setImageDrawable(null);
            }
            holder.name.setText(file.getName());
            if (file.getLastModified() > 0)
                holder.time.setText(FormatUtil.formatMills(file.getLastModified()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }


    private static class ViewHolder {
        ImageView icon;
        TextView name;
        TextView info;
        TextView time;
    }

    private void printInfo(SmbFile file) {
        try {
            Log.v(TAG, "Name: " + file.getName() + "," + file.isDirectory() + "," + file
                    .isHidden());
            Log.v(TAG, "uncPath: " + file.getUncPath());
            Log.v(TAG, "share: " + file.getShare());
            Log.v(TAG, "server: " + file.getServer());
            Log.v(TAG, "path: " + file.getPath());
            Log.v(TAG, "canonicalPath: " + file.getCanonicalPath());
            Log.v(TAG, "dfsPath: " + file.getDfsPath());
            Log.v(TAG, "parent: " + file.getParent());
            Log.v(TAG, "canRead: " + file.canRead());
            Log.v(TAG, "canWrite: " + file.canWrite());
            Log.v(TAG, "contentLength: " + file.getContentLength());
            Log.v(TAG, "lastModified: " + file.lastModified());
            Log.v(TAG, "getLastModified: " + file.getLastModified());
            Log.v(TAG, "length: " + file.length());
            Log.v(TAG, "type: " + file.getType());
            Log.v(TAG, "attributes: " + file.getAttributes());
            Log.v(TAG, "date: " + file.getDate());
            Log.v(TAG, "createTime: " + file.createTime());
            Log.v(TAG, "diskFreeSpace: " + file.getDiskFreeSpace());
            Log.v(TAG, "principal-name: " + file.getPrincipal().getName());
            Log.w(TAG, "--------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
