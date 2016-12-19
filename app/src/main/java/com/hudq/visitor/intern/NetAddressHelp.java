package com.hudq.visitor.intern;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;

/**
 * Created by hudq on 2016/12/14.
 */
public class NetAddressHelp {

    private static final String TAG = NetAddressHelp.class.getSimpleName();

    /**
     * 是否是wifi连接
     *
     * @param ctx Context
     * @return boolean
     */
    public static boolean isWifi(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 获取本地IP地址
     *
     * @return String
     */
    public static String getLocalIpByGPRS() {
        String ipAddress = "";
        try {
            //获取所有的本地可用的网络接口
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            if (en == null) {
                return ipAddress;
            }
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface networks = en.nextElement();
                // 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> address = networks.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (address.hasMoreElements()) {
                    InetAddress ip = address.nextElement();
                    if (!ip.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ip
                            .getHostAddress())) {
                        ipAddress = ip.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("", "获取本地ip地址失败");
            e.printStackTrace();
        }
        Log.d(TAG, "本机IP(gprs):" + ipAddress);
        return ipAddress;
    }

    /**
     * 获取本机IP，wifi开启下
     *
     * @param ctx Context
     * @return String
     */
    public static String getLocalIpByWifi(Context ctx) {
        WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) return null;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null) return null;
        // 获取32位整型IP地址
        int ipAddress = wifiInfo.getIpAddress();
        //返回整型地址转换成“*.*.*.*”地址
        String IP = String.format(Locale.getDefault(), "%d.%d.%d.%d",
                (ipAddress & 0xff),
                (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff),
                (ipAddress >> 24 & 0xff));
        Log.d(TAG, "本机IP(wifi):" + IP);
        return IP;
    }

    /**
     * 获取本机IP前缀头 <br/>
     * eg: 192.168.10.
     *
     * @param devAddress 本机IP地址
     * @return String
     */
    public static String getLocalIPPrefix(String devAddress) {
        if (devAddress == null || devAddress.length() == 0) {
            return null;
        }
        return devAddress.substring(0, devAddress.lastIndexOf(".") + 1);
    }
}
