package com.starrtc.demo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by zhangjt on 2017/8/9.
 */

public class StarNetUtil {
    /**
     * 当前没有可用网络
     */
    public static final int NONETWORK = 0;
    /**
     * 当前网络处于WIFI状态
     */
    public static final int WIFI = 1;
    /**
     * 当前是WAP网络
     */
    public static final int NET_WAP = 2;
    /**
     * 当前是2G网络
     */
    public static final int NET_2G = 3;
    /**
     * 当前是3G网络
     */
    public static final int NET_3G = 4;
    /**
     * 当前是4G网络
     */
    public static final int NET_4G = 5;
    /**
     * 手机网络
     */
    public static final int NET_MOBILE = 6;
    /**
     * 手机网络类型判断失败
     */
    public static final int NET_FAIL = -2;
    /**
     * 判断网络状况
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable()) {
            return false;
        }
        return true;
    }

    /**
     * 判断当前网络类型
     */
    public static int getNetworkType(Context context) {
        int mNetWorkType = -1;
        if (!isNetworkAvailable(context)) {
            mNetWorkType = NONETWORK;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String typeName = networkInfo.getTypeName();
            if (typeName.equalsIgnoreCase("WIFI")) {
                mNetWorkType = WIFI;
            } else if (typeName.equalsIgnoreCase("MOBILE")) {
                String proxyHost = Proxy.getDefaultHost();
                mNetWorkType = TextUtils.isEmpty(proxyHost) ? (isFastMobileNetwork(context) ? NET_3G : NET_2G) : NET_WAP;
            }
        }
        return mNetWorkType;
    }


    public static boolean isMobileNet(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String typeName = networkInfo.getTypeName();
            if (typeName.equalsIgnoreCase("MOBILE")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWifiNet(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String typeName = networkInfo.getTypeName();
            if (typeName.equalsIgnoreCase("WIFI")) {
                return true;
            }
        }
        return false;
    }

    private static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }

    public static int getPhoneNetworkType(Context context){
        int strNetworkType = -1;
        NetworkInfo networkInfo;
        ConnectivityManager connManager;
        if (!isNetworkAvailable(context)) {
            strNetworkType = NONETWORK;
        }
        try {
            connManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            networkInfo = connManager
                    .getActiveNetworkInfo();
        } catch (Exception e) {
            return -1;
        }
        if (networkInfo != null && networkInfo.isConnected()){

            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI){

                strNetworkType = WIFI;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){

                String _strSubTypeName = networkInfo.getSubtypeName();
                Log.e("GetNetworkType", "========_strSubTypeName : :====" + _strSubTypeName);

                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = NET_2G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = NET_3G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = NET_4G;
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")){
                            strNetworkType = NET_3G;
                        }
                        else {
                            //没有判断出网络
                            strNetworkType = NET_FAIL;
                            Log.e("GetNetworkType", "========Network Type : :====" + _strSubTypeName);
                        }
                        break;
                }
            }
        }
        return strNetworkType;
    }

    //获取Mac地址
    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 检查互联网地址是否可以访问
     *
     * @param address  要检查的域名或IP地址
     * @param callback 检查结果回调（是否可以ping通地址）{@see java.lang.Comparable<T>}
     */
    public static void isNetWorkAvailable(final String address, final Comparable<Boolean> callback) {
        final Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (callback != null) {
                    callback.compareTo(msg.arg1 == 0);
                }
            }

        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                Runtime runtime = Runtime.getRuntime();
                Message msg = new Message();
                try {
                    Process pingProcess = runtime.exec("/system/bin/ping -c 1 " + address);
                    InputStreamReader isr = new InputStreamReader(pingProcess.getInputStream());
                    BufferedReader buf = new BufferedReader(isr);
                    if (buf.readLine() == null) {
                        msg.arg1 = -1;
                    } else {
                        msg.arg1 = 0;
                    }
                    buf.close();
                    isr.close();
                } catch (Exception e) {
                    msg.arg1 = -1;
                    e.printStackTrace();
                } finally {
                    runtime.gc();
                    handler.sendMessage(msg);
                }
            }

        }).start();
    }

    public static String getIP(Context context){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address))
                    {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }
        catch (SocketException ex){
            ex.printStackTrace();
        }
        return null;
    }
}
