package com.starrtc.demo.demo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.starrtc.demo.R;
import com.starrtc.demo.database.CoreDB;
import com.starrtc.demo.database.HistoryBean;
import com.starrtc.demo.database.MessageBean;
import com.starrtc.demo.serverAPI.InterfaceUrls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhangjt on 2017/8/17.
 */

public class MLOC {
    public static Context appContext;
    public static String agentId = "stargWeHN8Y7";
    public static String authKey = "";
    public static String userId = "";

    public static String STAR_LOGIN_URL             = "ips2.starrtc.com:9920";
    public static String IM_SCHEDULE_URL            = "ips2.starrtc.com:9904";
    public static String LIVE_SRC_SCHEDULE_URL      = "ips2.starrtc.com:9929";
    public static String LIVE_VDN_SCHEDULE_URL      = "ips2.starrtc.com:9926";
    public static String CHAT_ROOM_SCHEDULE_URL     = "ips2.starrtc.com:9907";
    public static String VOIP_SCHEDULE_URL          = "voip2.starrtc.com:10086";
    public static String LIVE_PROXY_SCHEDULE_URL = "liveproxy.starrtc.com:19932";

    public static String VOIP_SERVER_URL          = "129.204.145.78:10086";
    public static String IM_SERVER_URL            = "129.204.145.78:19903";
    public static String CHATROOM_SERVER_URL      = "129.204.145.78:19906";
    public static String LIVE_VDN_SERVER_URL      = "129.204.145.78:19928";
    public static String LIVE_SRC_SERVER_URL      = "129.204.145.78:19931";
    public static String LIVE_PROXY_SERVER_URL    = "129.204.145.78:19932";

    public static String SERVER_TYPE_PUBLIC = "PUBLIC";
    public static String SERVER_TYPE_CUSTOM = "CUSTOM";
    public static String SERVER_TYPE = SERVER_TYPE_PUBLIC;

    public static final int CHATROOM_LIST_TYPE_CHATROOM = 0;
    public static final int CHATROOM_LIST_TYPE_LIVE = 1;
    public static final int CHATROOM_LIST_TYPE_LIVE_PUSH = 2;
    public static final int CHATROOM_LIST_TYPE_MEETING = 3;
    public static final int CHATROOM_LIST_TYPE_MEETING_PUSH = 4;
    public static final int CHATROOM_LIST_TYPE_CLASS = 5;
    public static final int CHATROOM_LIST_TYPE_CLASS_PUSH = 6;

    public static final String CHATROOM_LIST_TYPE_LIVE_ALL = CHATROOM_LIST_TYPE_LIVE+","+CHATROOM_LIST_TYPE_LIVE_PUSH;
    public static final String CHATROOM_LIST_TYPE_MEETING_ALL = CHATROOM_LIST_TYPE_MEETING+","+CHATROOM_LIST_TYPE_MEETING_PUSH;
    public static final String CHATROOM_LIST_TYPE_CLASS_ALL = CHATROOM_LIST_TYPE_CLASS+","+CHATROOM_LIST_TYPE_CLASS_PUSH;
    public static final String CHATROOM_LIST_TYPE_PUSH_ALL = CHATROOM_LIST_TYPE_LIVE_PUSH+","+CHATROOM_LIST_TYPE_MEETING_PUSH+","+CHATROOM_LIST_TYPE_CLASS_PUSH;

    public static Boolean hasLogout = false;

    public static boolean hasNewC2CMsg = false;
    public static boolean hasNewGroupMsg = false;
    public static boolean hasNewVoipMsg = false;
    public static boolean canPickupVoip = true;


    private static CoreDB coreDB;

    public static void init(Context context){
        appContext = context;
        if(coreDB==null){
            coreDB = new CoreDB();
        }
        userId = loadSharedData(context,"userId",userId);
        agentId = loadSharedData(context,"agentId",agentId);
        InterfaceUrls.setBaseUrl(loadSharedData(context,"workServer","https://api.starrtc.com/public"));
        STAR_LOGIN_URL = loadSharedData(context,"STAR_LOGIN_URL",STAR_LOGIN_URL);
        IM_SCHEDULE_URL = loadSharedData(context,"IM_SCHEDULE_URL",IM_SCHEDULE_URL);
        LIVE_SRC_SCHEDULE_URL = loadSharedData(context,"LIVE_SRC_SCHEDULE_URL",LIVE_SRC_SCHEDULE_URL);
        LIVE_VDN_SCHEDULE_URL = loadSharedData(context,"LIVE_VDN_SCHEDULE_URL",LIVE_VDN_SCHEDULE_URL);
        CHAT_ROOM_SCHEDULE_URL = loadSharedData(context,"CHAT_ROOM_SCHEDULE_URL",CHAT_ROOM_SCHEDULE_URL);
        VOIP_SCHEDULE_URL = loadSharedData(context,"VOIP_SCHEDULE_URL",VOIP_SCHEDULE_URL);
        LIVE_PROXY_SCHEDULE_URL = loadSharedData(context,"LIVE_PROXY_SCHEDULE_URL", LIVE_PROXY_SCHEDULE_URL);

        VOIP_SERVER_URL = loadSharedData(context,"VOIP_SERVER_URL",VOIP_SERVER_URL);
        IM_SERVER_URL = loadSharedData(context,"IM_SERVER_URL",IM_SERVER_URL);
        LIVE_SRC_SERVER_URL = loadSharedData(context,"LIVE_SRC_SERVER_URL",LIVE_SRC_SERVER_URL);
        LIVE_PROXY_SERVER_URL =  loadSharedData(context,"LIVE_PROXY_SERVER_URL", LIVE_PROXY_SERVER_URL);
        LIVE_VDN_SERVER_URL = loadSharedData(context,"LIVE_VDN_SERVER_URL",LIVE_VDN_SERVER_URL);
        CHATROOM_SERVER_URL = loadSharedData(context,"CHATROOM_SERVER_URL", CHATROOM_SERVER_URL);
        SERVER_TYPE = loadSharedData(context,"SERVER_TYPE", SERVER_TYPE_PUBLIC);
    }

    private static Boolean debug = true;
    public static void setDebug(Boolean b){
        debug = b;
    }

    public static void d(String tag,String msg){
        if(debug){
            Log.d("starSDK_demo_"+tag,msg);
        }
    }

    public static void e(String tag,String msg){
        Log.e("starSDK_demo_"+tag,msg);
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

    public static List<HistoryBean> getHistoryList(String type){
        if(coreDB!=null){
            return coreDB.getHistory(type);
        }else{
            return null;
        }
    }

    public static void addHistory(HistoryBean history, Boolean hasRead){
        if(coreDB!=null){
            coreDB.addHistory(history,hasRead);
        }
    }

    public static void updateHistory(HistoryBean history){
        if(coreDB!=null){
            coreDB.updateHistory(history);
        }
    }

    public static void removeHistory(HistoryBean history){
        if(coreDB!=null){
            coreDB.removeHistory(history);
        }
    }

    public static List<MessageBean> getMessageList(String conversationId){
        if(coreDB!=null){
            return coreDB.getMessageList(conversationId);
        }else{
            return null;
        }
    }

    public static void saveMessage(MessageBean messageBean){
        if(coreDB!=null){
            coreDB.setMessage(messageBean);
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

    public static String loadSharedData(Context context,String key,String defValue){
        SharedPreferences sp = context.getSharedPreferences("stardemo", Activity.MODE_PRIVATE);
        return sp.getString(key,defValue);
    }

    public static void saveWorkServer(String host){
        InterfaceUrls.BASE_URL = host;
        MLOC.saveSharedData(appContext,"workServer",InterfaceUrls.BASE_URL);
    }

    public static void saveUserId(String id){
        MLOC.userId = id;
        MLOC.saveSharedData(appContext,"userId",MLOC.userId);
    }
    public static void saveAgentId(String id){
        MLOC.agentId = id;
        saveSharedData(appContext,"agentId",id);
    }

    public static void saveLoginServerUrl(String loginServerUrl){
        MLOC.STAR_LOGIN_URL = loginServerUrl;
        saveSharedData(appContext,"STAR_LOGIN_URL",STAR_LOGIN_URL);
    }

    public static void saveIMSchduleUrl(String imSchduleUrl){
        MLOC.IM_SCHEDULE_URL = imSchduleUrl;
        saveSharedData(appContext,"IM_SCHEDULE_URL",IM_SCHEDULE_URL);
    }

    public static void saveSrcSchduleUrl(String srcSchduleUrl){
        MLOC.LIVE_SRC_SCHEDULE_URL = srcSchduleUrl;
        saveSharedData(appContext,"LIVE_SRC_SCHEDULE_URL",LIVE_SRC_SCHEDULE_URL);
    }

    public static void saveVdnSchduleUrl(String vdnSchduleUrl){
        MLOC.LIVE_VDN_SCHEDULE_URL = vdnSchduleUrl;
        saveSharedData(appContext,"LIVE_VDN_SCHEDULE_URL",LIVE_VDN_SCHEDULE_URL);
    }
    public static void saveProxySchduleUrl(String proxySchduleUrl){
        MLOC.LIVE_PROXY_SCHEDULE_URL = proxySchduleUrl;
        saveSharedData(appContext,"LIVE_PROXY_SCHEDULE_URL", LIVE_PROXY_SCHEDULE_URL);
    }

    public static void saveChatroomSchduleUrl(String chatroomSchduleUrl){
        MLOC.CHAT_ROOM_SCHEDULE_URL = chatroomSchduleUrl;
        saveSharedData(appContext,"CHAT_ROOM_SCHEDULE_URL",CHAT_ROOM_SCHEDULE_URL);
    }

    public static void saveVoipSchduleUrl(String voipSchduleUrl){
        MLOC.VOIP_SCHEDULE_URL = voipSchduleUrl;
        saveSharedData(appContext,"VOIP_SCHEDULE_URL",VOIP_SCHEDULE_URL);
    }
    public static void saveVoipServerUrl(String voipServerUrl){
        MLOC.VOIP_SERVER_URL = voipServerUrl;
        saveSharedData(appContext,"VOIP_SERVER_URL",VOIP_SERVER_URL);
    }

    public static void saveSrcServerUrl(String srcServerUrl){
        MLOC.LIVE_SRC_SERVER_URL = srcServerUrl;
        saveSharedData(appContext,"LIVE_SRC_SERVER_URL",LIVE_SRC_SERVER_URL);
    }

    public static void saveVdnServerUrl(String vdnServerUrl){
        MLOC.LIVE_VDN_SERVER_URL = vdnServerUrl;
        saveSharedData(appContext,"LIVE_VDN_SERVER_URL",LIVE_VDN_SERVER_URL);
    }

    public static void saveProxyServerUrl(String proxyServerUrl){
        MLOC.LIVE_PROXY_SERVER_URL = proxyServerUrl;
        saveSharedData(appContext,"LIVE_PROXY_SERVER_URL", LIVE_PROXY_SERVER_URL);
    }

    public static void saveChatroomServerUrl(String chatroomServerUrl){
        MLOC.CHATROOM_SERVER_URL = chatroomServerUrl;
        saveSharedData(appContext,"CHATROOM_SERVER_URL", CHATROOM_SERVER_URL);
    }

    public static void saveImServerUrl(String imServerUrl){
        MLOC.IM_SERVER_URL = imServerUrl;
        saveSharedData(appContext,"IM_SERVER_URL",IM_SERVER_URL);
    }

    public static void saveServerType(String serverType){
        MLOC.SERVER_TYPE = serverType;
        saveSharedData(appContext,"SERVER_TYPE",SERVER_TYPE);
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

    static Dialog[] dialogs = new Dialog[1];
    static Timer dialogTimer ;
    static TimerTask timerTask;
    public static void showDialog(final Context context, final JSONObject data){
        try {
            final int type = data.getInt("type");// 0:c2c,1:group,2:voip
            final String farId = data.getString("farId");// 对方ID
            String msg = data.getString("msg");// 提示消息

            if(dialogs[0]==null||dialogs[0].isShowing()==false){
                dialogs[0] = new Dialog(context, R.style.dialog_notify);
                dialogs[0].setContentView(R.layout.dialog_new_msg);
                Window win = dialogs[0].getWindow();
                win.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                win.setWindowAnimations(R.style.dialog_notify_animation);
                win.setGravity(Gravity.TOP);
                dialogs[0].setCanceledOnTouchOutside(true);
            }
            ((TextView) dialogs[0].findViewById(R.id.msg_info)).setText(msg);
            dialogs[0].findViewById(R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(dialogTimer!=null){
                        dialogTimer.cancel();
                        timerTask.cancel();
                        dialogTimer = null;
                        timerTask = null;
                    }
                    dialogs[0].dismiss();
                    dialogs[0] = null;
//                    if(type==0){
//                        //C2C
//                        Intent intent = new Intent(context,C2CListActivity.class);
//                        context.startActivity(intent);
//                    }else if(type==1){
//                        //Group
//                        Intent intent = new Intent(context, MessageGroupListActivity.class);
//                        context.startActivity(intent);
//                    }else if(type==2){
//                        //VOIP
//                        Intent intent = new Intent(context, VoipListActivity.class);
//                        context.startActivity(intent);
//                    }
                }
            });
            dialogs[0].show();

            if(dialogTimer!=null){
                dialogTimer.cancel();
                timerTask.cancel();
                dialogTimer = null;
                timerTask = null;
            }
            dialogTimer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if(dialogs[0]!=null&&dialogs[0].isShowing()){
                        dialogs[0].dismiss();
                        dialogs[0] = null;
                    }
                }
            };
            dialogTimer.schedule(timerTask,5000);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static int[] mHeadIconIds ;
    public static int getHeadImage(Context context,String userID){
        if(mHeadIconIds==null){
            TypedArray ar = context.getResources().obtainTypedArray(R.array.head_images);
            int len = ar.length();
            mHeadIconIds = new int[len];
            for (int i = 0; i < len; i++) {
                mHeadIconIds[i] = ar.getResourceId(i, 0);
            }
            ar.recycle();
        }

        if(userID.isEmpty()){
            return mHeadIconIds[70];
        }else{
            int intId = 0;
            char[] chars = userID.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                intId+=(int)chars[i];
            }
            return mHeadIconIds[intId%70];
        }
    }

}
