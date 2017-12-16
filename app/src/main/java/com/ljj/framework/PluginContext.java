package com.ljj.framework;

import java.lang.reflect.Method;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;

/**
 * Created by ljj on 2017/3/23.
 */

public class PluginContext extends ContextWrapper {
    private Resources resources;
    private String path;
    private LayoutInflater mInflater;
    private AssetManager assetManager;
    private String packageName;
    private ClassLoader classLoader;
    private Resources.Theme theme;
    Context base;
    PackageInfo packageInfo;

    public PluginContext(Context base,String path,String packageName,ClassLoader classLoader) {
        super(base);
        this.path=path;
        this.base=base;
        this.packageName=packageName;
        this.classLoader=classLoader;
        getResource(base,path);
        PackageManager pm = base.getPackageManager();
        packageInfo = pm.getPackageArchiveInfo(path, 1);
    }

    @Override
    public Resources getResources() {
        return resources;
    }

    public final void getResource(Context cnx,String path){
        try {
            AssetManager am=AssetManager.class.newInstance();
            Method add=am.getClass().getMethod("addAssetPath",String.class);
            add.setAccessible(true);
            add.invoke(am,path);
            assetManager=am;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Resources superRes = cnx.getResources();
        resources = new Resources(assetManager, superRes.getDisplayMetrics(),
                superRes.getConfiguration());
    }


    @Override
    public Object getSystemService(String name) {
     if (LAYOUT_INFLATER_SERVICE.equals(name)) {
          if (mInflater == null) {
                mInflater = LayoutInflater.from(base).cloneInContext(this);
            }
            return mInflater;
        }
        return base.getSystemService(name);
    }

    @Override
    public AssetManager getAssets() {
        return assetManager == null ? super.getAssets() : assetManager;
    }

    private int getId(String resType, String resName) {
        return resources.getIdentifier(resName, resType, packageName);
    }

    final public Drawable getDrawable(String resName) {
        return resources.getDrawable(getId("drawable", resName));
    }
    @Override
    final public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public Context createPackageContext(String packageName, int flags)
            throws PackageManager.NameNotFoundException {
        // TODO Auto-generated method stub
        return super.createPackageContext(packageName, flags);
    }

    @Override
    public Resources.Theme getTheme() {
            if (theme == null) {
                theme = resources.newTheme();
                // if (base.getTheme() != null) {
                // theme.setTo(base.getTheme());// 必须设置，view基础属性都在这里定义
                // }
                int srcTheme=packageInfo.applicationInfo.theme;
                int themeId=selectDefaultTheme(srcTheme,packageInfo == null
                    ? 0 : packageInfo.applicationInfo.targetSdkVersion);
                theme.applyStyle(themeId, true);
            }
            return theme;
    }
    public static int selectDefaultTheme(int curTheme, int targetSdkVersion) {
        return selectSystemTheme(curTheme, targetSdkVersion, android.R.style.Theme,
            android.R.style.Theme_Holo, android.R.style.Theme_DeviceDefault_Light_DarkActionBar,
            android.R.style.Theme_DeviceDefault);
    }

    public static int selectSystemTheme(int curTheme, int targetSdkVersion,
        int orig, int holo, int dark, int deviceDefault) {
        if (curTheme != 0) {
            return curTheme;
        }
        if (targetSdkVersion < Build.VERSION_CODES.HONEYCOMB) {
            return orig;
        }
        if (targetSdkVersion < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return holo;
        }
        if (targetSdkVersion < 24) {
            // 7.0+
            return dark;
        }
        return deviceDefault;
    }

}
