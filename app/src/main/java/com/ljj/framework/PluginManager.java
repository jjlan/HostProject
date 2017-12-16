package com.ljj.framework;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import android.app.Instrumentation;
import android.content.Context;

import com.ljj.framework.Utils.IOUtils;

/**
 * Created by lanjunjian on 2017/4/18.
 */

public class PluginManager {
  private Map<String,PluginApk> plugins=new HashMap<>();
  private static PluginManager instance=new PluginManager();
  private Context context;
  private String packageName="com.ljj.plugintest";
  private PluginManager(){

  }
  public static PluginManager getInstance(){
    return instance;
  }
   public void init(Context context){
       this.context=context;
      String dexPath=context.getFilesDir().getAbsolutePath()+ File.separator+"plugin.apk";
      IOUtils.copyPluginFromAssets(context,"app-debug.apk",dexPath);
      PluginApk plugin=new PluginApk(packageName,dexPath);
      plugins.put(packageName,plugin);
      hookInstrumentation();
   }
   public Context getHostContext(){
     return context;
   }
  public void hookInstrumentation(){
    try {
      Class<?> activityThreadClass= Class.forName("android.app.ActivityThread");
      Method currentActivityThreadMetnod=activityThreadClass.getDeclaredMethod("currentActivityThread");
      currentActivityThreadMetnod.setAccessible(true);
      Object currentActivityThread=currentActivityThreadMetnod.invoke(null);
      Field mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
      mInstrumentationField.setAccessible(true);
      Instrumentation mInstrumentation = (Instrumentation) mInstrumentationField.get(currentActivityThread);
      if (!(mInstrumentation instanceof PluginInstrumentation)) {
        PluginInstrumentation pluginInstrumentation = new PluginInstrumentation(plugins.get(packageName),mInstrumentation);
        mInstrumentationField.set(currentActivityThread, pluginInstrumentation);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
