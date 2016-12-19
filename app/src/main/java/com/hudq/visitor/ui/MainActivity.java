package com.hudq.visitor.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hudq.visitor.R;
import com.hudq.visitor.adapter.DeviceListAdapter;
import com.hudq.visitor.bean.DeviceInfo;
import com.hudq.visitor.intern.$$;
import com.hudq.visitor.intern.NetAddressHelp;
import com.hudq.visitor.intern.ScanDeviceEngine;

import java.lang.ref.WeakReference;
import java.net.UnknownHostException;
import java.util.List;

import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbSession;

/**
 * Created by hudq on 2016/12/15.
 */
public class MainActivity extends Activity {

    private static final String TAG = "MainUI";
    private static final int MSG_SUCCESS = 0x01;
    private static final int MSG_FAIL = 0x10;
    private ScanDeviceEngine engine;
    private ListView listview;
    private DeviceListAdapter adapter;
    private List<DeviceInfo> cacheData;
    private MyHandler handler = new MyHandler(this);

    //测试
    public static String sUserName;
    public static String sPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        engine = new ScanDeviceEngine(this);
        ((TextView) findViewById(R.id.tv_local_ip)).setText(" 当前设备IP: " + NetAddressHelp
                .getLocalIpByWifi(this));
        listview = (ListView) findViewById(R.id.device_listview);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter == null) return;
                DeviceInfo o = adapter.getItem(position);
                showInputDialog(o);
            }
        });
    }

    public void startScan(View view) {
        searchDevices();
    }

    private void searchDevices() {
        final ProgressDialog dialog = ProgressDialog.show(this, "搜索", "正在搜索设备...");
        engine.startScanning();
        engine.setScannerListener(new ScanDeviceEngine.ScannerListener() {

            @Override
            public void onScanFinished(List<DeviceInfo> result) {
                if (result != null && !result.isEmpty()) {
                    cacheData = result;
                }
                if (result == null || result.isEmpty()) {
                    result = cacheData;
                }
                adapter = new DeviceListAdapter(MainActivity.this, result);
                listview.setAdapter(adapter);

                if (dialog != null) dialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        engine.destory();
    }

    private void showInputDialog(final DeviceInfo o) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        ((TextView) view.findViewById(R.id.dialog_tv_title)).setText("连接到：" + o.getName());
        final EditText name = (EditText) view.findViewById(R.id.dialog_et_name);
        final EditText pwd = (EditText) view.findViewById(R.id.dialog_et_pwd);
        view.findViewById(R.id.dialog_btn_negative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.dialog_btn_positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                sUserName = name.getText().toString().trim();
                sPassword = pwd.getText().toString().trim();
                $$.execute(new UsRunnable(o.getIp()));
            }
        });
    }

    private class UsRunnable implements Runnable {
        private String ip;

        private UsRunnable(String ip) {
            this.ip = ip;
        }

        @Override
        public void run() {
            connectPC(ip);
        }
    }

    private void connectPC(String ip) {
        String username = sUserName;
        String password = sPassword;
        try {
            //用户名和密码鉴权,使用此种方法校验name和password，避免password出现特殊字符导致错误
            UniAddress domain = UniAddress.getByName(ip);
            SmbSession.logon(domain, new NtlmPasswordAuthentication(ip, username, password));
            //成功，跳转
            Message msg = Message.obtain();
            msg.obj = ip;
            msg.what = MSG_SUCCESS;
            handler.sendMessage(msg);
        } catch (SmbException e) {
            e.printStackTrace();
            Log.e(TAG, "连接失败");
            handler.sendEmptyMessage(MSG_FAIL);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.e(TAG, "UnknownHost");
            handler.sendEmptyMessage(MSG_FAIL);
        }
    }

    private static class MyHandler extends Handler {
        private WeakReference<MainActivity> reference;

        private MyHandler(MainActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (reference.get() == null) return;
            MainActivity activity = reference.get();
            if (msg.what == MSG_SUCCESS) {
                String ip = (String) msg.obj;
                String path = "smb://" + ip;
                activity.startActivity(new Intent(activity, PCInfosActivity.class)
                        .putExtra("path", path)
                        .putExtra("ip", ip));
            } else if (msg.what == MSG_FAIL) {
                Toast.makeText(activity.getApplicationContext(), "连接远程设备失败", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
}
