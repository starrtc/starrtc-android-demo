package com.starrtc.demo.demo.thirdstream;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.demo.demo.MLOC;
import com.starrtc.demo.listener.XHChatroomManagerListener;
import com.starrtc.demo.serverAPI.InterfaceUrls;
import com.starrtc.demo.utils.AEvent;
import com.starrtc.starrtcsdk.api.XHChatroomManager;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.api.XHConstants;
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class RtspTestActivity extends BaseActivity {

    private String name;
    private String rtsp;
    private String roomId;
    private XHChatroomManager chatroomManager;
    private int createType = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtsp_test_create);
        ((TextView)findViewById(R.id.title_text)).setText("第三方拉流");
        findViewById(R.id.title_left_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.title_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAndPush(1);
            }
        });
        findViewById(R.id.yes_btn_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAndPush(2);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        AEvent.addListener(AEvent.AEVENT_RTSP_FORWARD,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_ERROR,this);
    }
    @Override
    public void onPause() {
        super.onPause();
        AEvent.removeListener(AEvent.AEVENT_RTSP_FORWARD,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_ERROR,this);
    }
    @Override
    public void dispatchEvent(String aEventID, boolean success, final Object eventObj){
        super.dispatchEvent(aEventID,success,eventObj);
        if(aEventID.equals(AEvent.AEVENT_RTSP_FORWARD)){
            if(success){
                try {
                    JSONObject jsonObject = new JSONObject((String) eventObj);
                    if(jsonObject.has("channelId")){
                        if(!MLOC.SERVER_TYPE.equals(MLOC.SERVER_TYPE_PUBLIC)){
                            try {
                                if(createType==1) {
                                    JSONObject info = new JSONObject();
                                    info.put("id",jsonObject.getString("channelId")+roomId);
                                    info.put("creator",MLOC.userId);
                                    info.put("name",name);
                                    info.put("rtsp",rtsp);
                                    info.put("type",MLOC.CHATROOM_LIST_TYPE_MEETING_PUSH);
                                    String infostr = info.toString();
                                    infostr = URLEncoder.encode(infostr,"utf-8");
                                    chatroomManager.saveToList(MLOC.userId, MLOC.CHATROOM_LIST_TYPE_MEETING_PUSH, roomId, infostr, null);
                                }else {
                                    JSONObject info = new JSONObject();
                                    info.put("id",jsonObject.getString("channelId")+roomId);
                                    info.put("creator",MLOC.userId);
                                    info.put("name",name);
                                    info.put("rtsp",rtsp);
                                    info.put("type",MLOC.CHATROOM_LIST_TYPE_LIVE_PUSH);
                                    String infostr = info.toString();
                                    infostr = URLEncoder.encode(infostr,"utf-8");
                                    chatroomManager.saveToList(MLOC.userId,MLOC.CHATROOM_LIST_TYPE_LIVE_PUSH,roomId,infostr,null);
                                }
                                MLOC.d("@@@@@@@","saveTo");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MLOC.showMsg(RtspTestActivity.this,"拉流成功");
                RtspTestActivity.this.finish();
            }else{
                MLOC.showMsg(RtspTestActivity.this,"拉流失败"+(String)eventObj);
            }
        }else if(aEventID .equals(AEvent.AEVENT_CHATROOM_ERROR)){
            MLOC.showMsg(RtspTestActivity.this,"ERROR:"+eventObj);
        }
    }

    private void createAndPush(final int type){
        name = ((EditText)findViewById(R.id.targetid_input)).getText().toString();
        rtsp = ((EditText)findViewById(R.id.rtsp_input)).getText().toString();
        createType = type;
        if(TextUtils.isEmpty(name)){
            MLOC.showMsg(RtspTestActivity.this,"名字不能为空");
        }else if(TextUtils.isEmpty(rtsp)){
            MLOC.showMsg(RtspTestActivity.this,"拉流地址不能为空");
        }else{
            chatroomManager = XHClient.getInstance().getChatroomManager();
            chatroomManager.addListener(new XHChatroomManagerListener());
            chatroomManager.createChatroom(name,XHConstants.XHChatroomType.XHChatroomTypePublic, new IXHResultCallback() {
                @Override
                public void success(final Object data) {
                    roomId = data.toString();
                    if(MLOC.SERVER_TYPE.equals(MLOC.SERVER_TYPE_CUSTOM)){
                        InterfaceUrls.demoPushRtsp(MLOC.LIVE_PROXY_SERVER_URL,name,roomId,type,rtsp);
                    }else{
                        InterfaceUrls.demoPushRtsp(MLOC.LIVE_PROXY_SCHEDULE_URL,name,roomId,type,rtsp);
                    }

                }

                @Override
                public void failed(String errMsg) {
                    final String err = errMsg;
                    MLOC.showMsg(RtspTestActivity.this,err.toString());
                }
            });
        }
    }

}
