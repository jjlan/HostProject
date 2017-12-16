package com.ljj.host.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ljj.framework.PluginManager;
import com.ljj.host.R;

import dalvik.system.DexClassLoader;

public class MainActivity extends Activity {

    private DexClassLoader mClassLoader;
    private String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PluginManager.getInstance().init(getApplicationContext());
    }



    public void startPlugin(View v){
        Intent intent=new Intent();
        intent.setClassName(this,"com.ljj.plugintest.MainActivity");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
    }
}
