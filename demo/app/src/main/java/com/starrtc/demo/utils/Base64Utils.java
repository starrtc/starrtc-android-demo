package com.starrtc.demo.utils;

import android.util.Base64;

public class Base64Utils {
    public static String base64String(String str){
        return Base64.encodeToString(str.getBytes(),Base64.DEFAULT);
    }
    public static String decodeBase64(String str){
        return new String(Base64.decode(str,Base64.DEFAULT));
    }
}
