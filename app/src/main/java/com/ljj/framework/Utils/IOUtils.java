package com.ljj.framework.Utils;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;

/**
 * Created by lanjunjian on 2017/4/18.
 */

public class IOUtils {
  public static void copyPluginFromAssets(Context context,String apkName,String desDir){
    try{
      InputStream in=context.getAssets().open(apkName);
      OutputStream os=new FileOutputStream(desDir);
      byte[] temp=new byte[1024];
      int len=-1;
      while((len=in.read(temp))!=-1){
        os.write(temp,0,len);
      }
      in.close();
      os.flush();
      os.close();
    }catch (Exception e){
    }
  }

}
