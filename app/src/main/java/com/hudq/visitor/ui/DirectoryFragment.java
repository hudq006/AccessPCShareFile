package com.hudq.visitor.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hudq.visitor.R;
import com.hudq.visitor.adapter.DirectoryAdapter;
import com.hudq.visitor.intern.$$;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbSession;

/**
 *
 */
public class DirectoryFragment extends Fragment {

    interface FileClickListener {
        void onFileClicked(SmbFile file);
    }

    private static final String ARG_IP = "arg_current_ip";
    private static final String ARG_FILE_PATH = "arg_file_path";
    private ListView listView;

    private String currentIp;
    private String filePath;

    private List<SmbFile> smbFiles = new ArrayList<>();
    private DirectoryAdapter adapter;
    private OxHandler handler = new OxHandler(this);
    private FileClickListener listener;

    public static DirectoryFragment getInstance(String ip, String path) {
        DirectoryFragment instance = new DirectoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IP, ip);
        args.putString(ARG_FILE_PATH, path);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_directory, container, false);
        listView = (ListView) view.findViewById(R.id.list_listview);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (FileClickListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentIp = getArguments().getString(ARG_IP);
        filePath = getArguments().getString(ARG_FILE_PATH);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    listener.onFileClicked(adapter.getItem(position));
                }
            }
        });
        $$.execute(new ExecRunnable(currentIp, filePath));
    }

    private class ExecRunnable implements Runnable {
        private String ip;
        private String path;

        private ExecRunnable(String ip, String path) {
            this.ip = ip;
            this.path = path;
        }

        @Override
        public void run() {
            connectFile(ip, path);
        }
    }

    private void connectFile(String ip, String filePath) {
        smbFiles.clear();
        try {
            UniAddress domain = UniAddress.getByName(ip);
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(ip,
                    MainActivity.sUserName, MainActivity.sPassword);
            SmbSession.logon(domain, auth);

            SmbFile smbFile = new SmbFile(filePath, auth);
            smbFile.connect();
            SmbFile[] files = smbFile.listFiles();
            for (SmbFile file : files) {
                Message message = Message.obtain();
                message.obj = file;
                message.what = 100; //成功
                handler.sendMessage(message);
            }
            handler.sendEmptyMessage(200); //完毕
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class OxHandler extends android.os.Handler {
        private WeakReference<DirectoryFragment> reference;

        private OxHandler(DirectoryFragment f) {
            reference = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DirectoryFragment f = reference.get();
            if (f == null) return;
            if (msg.what == 100) {
                f.smbFiles.add(((SmbFile) msg.obj));
            } else if (msg.what == 200) {
                f.adapter = new DirectoryAdapter(f.getActivity(), f.smbFiles);
                f.listView.setAdapter(f.adapter);
            }
        }
    }
}
