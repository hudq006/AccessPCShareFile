package com.hudq.visitor.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hudq.visitor.R;
import com.hudq.visitor.bean.DeviceInfo;

import java.util.List;

/**
 * 设备列表adapter
 *
 * Created by hudq on 2016/12/14.
 */
public class DeviceListAdapter extends BaseAdapter {

    private Context ctx;
    private List<DeviceInfo> infos;

    public DeviceListAdapter(Context ctx, List<DeviceInfo> infos) {
        this.ctx = ctx;
        this.infos = infos;
    }

    @Override
    public int getCount() {
        return infos != null ? infos.size() : 0;
    }

    @Override
    public DeviceInfo getItem(int position) {
        return infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(ctx, R.layout.item_device_list, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.item_dev_name);
            holder.ip = (TextView) convertView.findViewById(R.id.item_dev_ip);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DeviceInfo o = infos.get(position);
        holder.name.setText(o.getName());
        holder.ip.setText(o.getIp());
        return convertView;
    }


    private static class ViewHolder {
        TextView name;
        TextView ip;
    }
}
