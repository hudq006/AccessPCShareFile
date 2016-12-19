package com.hudq.visitor.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.hudq.visitor.R;
import com.hudq.visitor.intern.$$;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

/**
 * Created by hudq on 2016/12/15.
 */
public class PCInfosActivity extends Activity implements DirectoryFragment.FileClickListener {

    private static final String TAG = "PCInfoUI";

    private String currentIP;
    private String currentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pcinfos);
        currentIP = getIntent().getStringExtra("ip");
        currentPath = getIntent().getStringExtra("path");

        getFragmentManager().beginTransaction()
                .add(R.id.container, DirectoryFragment.getInstance(currentIP, currentPath))
                .commit();
    }


    private void addFragmentToBackStack(String ip, String path) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, DirectoryFragment.getInstance(ip, path))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onFileClicked(SmbFile file) {
        String canonicalPath = file.getCanonicalPath();
        Log.d(TAG, "canonicalPath: " + canonicalPath);
        try {
            if (file.isHidden()) return;

            if (file.isDirectory()) {
                addFragmentToBackStack(currentIP, canonicalPath);
                currentPath = canonicalPath;
            } else if (file.isFile()) {
                Toast.makeText(getApplicationContext(), "保存文件：" + file.getName(), Toast
                        .LENGTH_SHORT).show();
                $$.execute(new MyRunnable(file));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private class MyRunnable implements Runnable {
        private SmbFile file;

        private MyRunnable(SmbFile file) {
            this.file = file;
        }

        @Override
        public void run() {
            save2Local(file);
        }
    }

    private void save2Local(SmbFile file) {
        String sdRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        File localFile = new File(sdRoot + "/Android/" + file.getName());
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(new SmbFileInputStream(file));
            out = new BufferedOutputStream(new FileOutputStream(localFile));
            byte[] buffer = new byte[2048];
            while ((in.read(buffer)) != -1) {
                out.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "保存到本地成功！");
        Toast.makeText(getApplicationContext(), "文件保存到本地成功！", Toast.LENGTH_SHORT).show();
    }
}
