package com.hudq.visitor.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hudq on 2016/12/14.
 */

public class DeviceInfo implements Parcelable {

    private String name;
    private String ip;

    public DeviceInfo(String name, String ip) {
        this.name = name;
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.ip);
    }

    private DeviceInfo(Parcel in) {
        this.name = in.readString();
        this.ip = in.readString();
    }

    public static final Creator<DeviceInfo> CREATOR = new Creator<DeviceInfo>() {
        @Override
        public DeviceInfo createFromParcel(Parcel source) {
            return new DeviceInfo(source);
        }

        @Override
        public DeviceInfo[] newArray(int size) {
            return new DeviceInfo[size];
        }
    };
}
