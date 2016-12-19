package com.hudq.visitor;

import android.app.Application;

/**
 * Created by hudq on 2016/12/16.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
    }

    private void initConfig() {
        System.setProperty("jcifs.smb.client.dfs.disabled", "true");
        System.setProperty("jcifs.smb.client.soTimeout", "60000");
        System.setProperty("jcifs.smb.client.responseTimeout", "60000");
        //......
    }

}
