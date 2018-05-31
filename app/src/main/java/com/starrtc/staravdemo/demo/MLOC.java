package com.starrtc.staravdemo.demo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangjt on 2017/8/17.
 */

public class MLOC {
    public static Context appContext;
    public static String agentId = "BjR6QV3vUJ4d";
    public static String authKey;
    public static String userId;

    public static void init(Context context){
        appContext = context;
    }

    public static void d(String tag,String msg){
        Log.d("starSDK_demo_"+tag,msg);
    }

    private static Toast mToast;
    public static void showMsg(String str){
        try {
            if (mToast != null) {
                mToast.setText(str);
                mToast.setDuration(Toast.LENGTH_SHORT);
            } else {
                mToast = Toast.makeText(appContext, str, Toast.LENGTH_SHORT);
            }
            mToast.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public static void showMsg(Context context ,String str){
        try {
            if (mToast != null) {
                mToast.setText(str);
                mToast.setDuration(Toast.LENGTH_SHORT);
            } else {
                mToast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
            }
            mToast.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void saveSharedData(Context context,String key,String value){
        SharedPreferences sp = context.getSharedPreferences("stardemo", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String loadSharedData(Context context,String key){
        SharedPreferences sp = context.getSharedPreferences("stardemo", Activity.MODE_PRIVATE);
        return sp.getString(key,"");
    }

    public static void saveC2CUserId(Context context,String uid){
        String history = MLOC.loadSharedData(context,"c2cHistory");
        if(history.length()>0){
            String[] arr = history.split(",,");
            String newHistory = "";
            for(int i = 0;i<arr.length;i++){
                if(i==0){
                    if(arr[i].equals(uid))return;
                    newHistory+=arr[i];
                }else{
                    if(arr[i].equals(uid))continue;
                    newHistory+=",,"+arr[i];
                }
            }
            if(newHistory.length()==0){
                newHistory = uid;
            }else{
                newHistory = uid+",,"+newHistory;
            }
            MLOC.saveSharedData(context,"c2cHistory",newHistory);
        }else{
            MLOC.saveSharedData(context,"c2cHistory",uid);
        }
    }
    public static void cleanC2CUserId(Context context){
        MLOC.saveSharedData(context,"c2cHistory","");
    }

    public static void saveVoipUserId(Context context,String uid){
        String history = MLOC.loadSharedData(context,"voipHistory");
        if(history.length()>0){
            String[] arr = history.split(",,");
            String newHistory = "";
            for(int i = 0;i<arr.length;i++){
                if(i==0){
                    if(arr[i].equals(uid))return;
                    newHistory+=arr[i];
                }else{
                    if(arr[i].equals(uid))continue;
                    newHistory+=",,"+arr[i];
                }
            }
            if(newHistory.length()==0){
                newHistory = uid;
            }else{
                newHistory = uid+",,"+newHistory;
            }
            MLOC.saveSharedData(context,"voipHistory",newHistory);
        }else{
            MLOC.saveSharedData(context,"voipHistory",uid);
        }
    }
    public static void cleanVoipUserId(Context context){
        MLOC.saveSharedData(context,"voipHistory","");
    }

}
