package com.ljj.framework;

import java.io.File;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import dalvik.system.DexClassLoader;

/**
 * Created by lanjunjian on 2017/4/18.
 */

public class PluginApk {
  private ClassLoader classLoader;
  private String packageName;
  private PluginContext pluginContext;
  private String apkPath;

  public PluginApk(String packageName,String apkPath){
    this.apkPath=apkPath;
    this.packageName=packageName;
    classLoader=createClassLoader(apkPath);
    pluginContext=new PluginContext(PluginManager.getInstance().getHostContext(),apkPath,packageName,classLoader);
    getPluginTheme(pluginContext);
  }
  private ClassLoader createClassLoader(String apkPath){
    //作为odex的释放路径
    String optimizedDirectory= PluginManager.getInstance().getHostContext().getFilesDir().getAbsolutePath()+ File.separator+"plugin"+File.separator;
    //此目录得提前创建，不然容易出错
    if(!new File(optimizedDirectory).exists()){
      new File(optimizedDirectory).mkdirs();
    }
    ClassLoader parent=PluginManager.getInstance().getHostContext().getClassLoader();
    return new DexClassLoader(apkPath,optimizedDirectory,null,parent);
  }
  public String getPackageName(){
     return packageName;
  }
  public ClassLoader getPluginClassLoader(){
    return classLoader;
  }
  public PluginContext getPluginContext(){
    return pluginContext;
  }

  public String getApkPath(){
    return apkPath;
  }
  private void  getPluginTheme(Context context){
    PackageManager pm=context.getPackageManager();
    PackageInfo info=pm.getPackageArchiveInfo(apkPath,PackageManager.GET_ACTIVITIES);
    int themeId=info.applicationInfo.theme;
    Log.i("ljj", "getPluginTheme: "+themeId);
    context.getTheme().applyStyle(themeId,true);
  }
  public  int getPluginThemeId(){
    PackageManager pm=pluginContext.getPackageManager();
    PackageInfo info=pm.getPackageArchiveInfo(apkPath,PackageManager.GET_ACTIVITIES);
    int themeId=info.applicationInfo.theme;
    return themeId;

  }
}
