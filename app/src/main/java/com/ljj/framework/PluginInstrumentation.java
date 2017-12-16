package com.ljj.framework;

import java.lang.reflect.Method;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;

import com.ljj.framework.Utils.ClassUtils;

/**
 * Created by ljj on 2017/3/12.
 */

public class PluginInstrumentation extends Instrumentation {


  private Instrumentation originalInstrumentation;
  private PluginApk plugin;

  public PluginInstrumentation(PluginApk plugin, Instrumentation originalInstrumentation) {
    this.plugin = plugin;
    this.originalInstrumentation = originalInstrumentation;
  }


  public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target,
      Intent intent, int requestCode, Bundle options) {
    Log.i("ljj", "execStartActivity:-----------------" + intent.getComponent().getClassName());
    intent.putExtra("className", intent.getComponent().getClassName());
    intent.setClassName(who, "com.ljj.framework.PluginActivity");

    try {
      Class<Instrumentation> cl =
          (Class<Instrumentation>) this.getClass().getClassLoader().loadClass("android.app.Instrumentation");
      Method execStartActivity = Instrumentation.class
          .getDeclaredMethod("execStartActivity", Context.class, IBinder.class, IBinder.class, Activity.class,
              Intent.class, int.class, Bundle.class);
      execStartActivity.setAccessible(true);
      ActivityResult r = (ActivityResult) execStartActivity
          .invoke(originalInstrumentation, who, contextThread, token, target, intent, requestCode, options);
      return r;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException,
      IllegalAccessException, ClassNotFoundException {
    className = (String) intent.getStringExtra("className");
    cl = plugin.getPluginClassLoader();
    return originalInstrumentation.newActivity(cl, className, intent);
  }

  @Override
  public void callActivityOnCreate(Activity activity, Bundle icicle) {
    ClassUtils.replaceField(ContextWrapper.class.getName(), "mBase", activity, plugin.getPluginContext());
    ClassUtils.replaceField("android.view.ContextThemeWrapper", "mResources", activity,
        plugin.getPluginContext().getResources());
    //ClassUtils.replaceField("android.view.ContextThemeWrapper", "mTheme",
    //    activity, null);
    //activity.setTheme(plugin.getPluginThemeId());
    ClassUtils.replaceField("android.view.ContextThemeWrapper", "mTheme", activity, plugin.getPluginContext().getTheme());
    ClassUtils.replaceField("android.view.ContextThemeWrapper", "mInflater", activity, null);
    LayoutInflater in = LayoutInflater.from(activity);
    ClassUtils.replaceField("com.android.internal.policy.impl.PhoneWindow", "mLayoutInflater", activity.getWindow(), in);
    originalInstrumentation.callActivityOnCreate(activity, icicle);
  }

  @Override
  public void callActivityOnPause(Activity activity) {
    originalInstrumentation.callActivityOnPause(activity);
  }

  @Override
  public void callActivityOnDestroy(Activity activity) {
    originalInstrumentation.callActivityOnDestroy(activity);
  }

  @Override
  public void callActivityOnResume(Activity activity) {
    originalInstrumentation.callActivityOnResume(activity);
  }
}
