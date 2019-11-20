package com.starrtc.demo.demo.miniclass;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.demo.demo.MLOC;
import com.starrtc.demo.listener.XHLiveManagerListener;
import com.starrtc.demo.serverAPI.InterfaceUrls;
import com.starrtc.demo.demo.videomeeting.ViewPosition;
import com.starrtc.demo.utils.AEvent;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.api.XHConstants;
import com.starrtc.starrtcsdk.api.XHLiveItem;
import com.starrtc.starrtcsdk.api.XHLiveManager;
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback;
import com.starrtc.starrtcsdk.core.audio.StarRTCAudioManager;
import com.starrtc.starrtcsdk.core.im.message.XHIMMessage;
import com.starrtc.starrtcsdk.core.player.StarPlayer;
import com.starrtc.starrtcsdk.core.player.StarWhitePanel2;
import com.starrtc.starrtcsdk.core.pusher.XHCameraRecorder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MiniClassActivity extends BaseActivity{

    public static String CLASS_ID = "CLASS_ID";             //ID
    public static String CLASS_NAME = "CLASS_NAME";         //名称
    public static String CLASS_TYPE = "CLASS_TYPE";         //类型
    public static String CLASS_CREATOR = "CLASS_CREATOR";   //创建者

    private String classId;
    private String className;
    private String creatorId;

    private TextView vMeetingName;

    private List<XHIMMessage> mDatas;
    private MyChatroomListAdapter mAdapter ;
    private ListView vMsgList;
    private RelativeLayout vPlayerView;
    private TextView vLinkBtn;
    private ImageView vCameraBtn;
    private ImageView vMicBtn;
    private ImageView vCleanBtn;
    private ImageView vRevokeBtn;
    private ImageView vLaserPenBtn;
    private ImageView vSelectColorBtn;
    private View vSelectColorView;

    private ArrayList<ViewPosition> mPlayerList;
    private int borderW = 0;
    private int borderH = 0;

    private XHLiveManager classManager;
    private StarWhitePanel2 vPaintPlayer;

    private Boolean isPortrait = true;
    private Boolean isUploader = false;

    private StarRTCAudioManager starRTCAudioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);

        starRTCAudioManager = StarRTCAudioManager.create(this);
        starRTCAudioManager.start(new StarRTCAudioManager.AudioManagerEvents() {
            @Override
            public void onAudioDeviceChanged(StarRTCAudioManager.AudioDevice selectedAudioDevice, Set availableAudioDevices) {

            }
        });

        classManager = XHClient.getInstance().getLiveManager(this);
        classManager.setRtcMediaType(XHConstants.XHRtcMediaTypeEnum.STAR_RTC_MEDIA_TYPE_VIDEO_AND_AUDIO);
        classManager.setRecorder(new XHCameraRecorder());
        classManager.addListener(new XHLiveManagerListener());

        DisplayMetrics dm = getResources().getDisplayMetrics();
        if(dm.heightPixels>dm.widthPixels){
            isPortrait = true;
            setContentView(R.layout.activity_mini_class);
        }else{
            isPortrait = false;
            setContentView(R.layout.activity_mini_class_landscape);
        }
        vPlayerView = (RelativeLayout) findViewById(R.id.view1);
        mPlayerList = new ArrayList<>();
        if(isPortrait){
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dm.widthPixels,dm.widthPixels/4*3);
            vPlayerView.setLayoutParams(lp);
            borderW = dm.widthPixels;
            borderH = dm.widthPixels/4*3;
        }else{
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dm.heightPixels/4/9*16,dm.heightPixels);
            vPlayerView.setLayoutParams(lp);
            borderW = dm.heightPixels/4/9*16;
            borderH = dm.heightPixels;
        }

        addListener();
        classId = getIntent().getStringExtra(CLASS_ID);
        className = getIntent().getStringExtra(CLASS_NAME);
        creatorId = getIntent().getStringExtra(CLASS_CREATOR);
        vMeetingName = (TextView) findViewById(R.id.live_id_text);
        vMeetingName.setText("ID："+ className);
        vPaintPlayer = (StarWhitePanel2) findViewById(R.id.painter_view);
        vPaintPlayer.setImageHost("api.starrtc.com");

        findViewById(R.id.chat_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.white_panel_view).setVisibility(View.INVISIBLE);
                findViewById(R.id.chat_message_view).setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.panel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.white_panel_view).setVisibility(View.VISIBLE);
                findViewById(R.id.chat_message_view).setVisibility(View.INVISIBLE);
            }
        });
        findViewById(R.id.send_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = ((EditText)findViewById(R.id.id_input)).getText().toString();
                if(!TextUtils.isEmpty(txt)){
                    sendChatMsg(txt);
                    ((EditText)findViewById(R.id.id_input)).setText("");
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(((EditText)findViewById(R.id.id_input)).getWindowToken(), 0);
            }
        });

        vCameraBtn = (ImageView) findViewById(R.id.camera_btn);
        vMicBtn = (ImageView) findViewById(R.id.mic_btn);
        vLinkBtn = (TextView) findViewById(R.id.link_btn);
        vCleanBtn = (ImageView) findViewById(R.id.clean_btn);
        vCleanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vPaintPlayer.clean();
            }
        });
        vRevokeBtn = (ImageView) findViewById(R.id.revoke_btn);
        vRevokeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vPaintPlayer.revoke();
            }
        });
        vLaserPenBtn = (ImageView) findViewById(R.id.laser_btn);
        vLaserPenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vLaserPenBtn.isSelected()){
                    vLaserPenBtn.setSelected(false);
                    vPaintPlayer.laserPenOff();
                }else{
                    vLaserPenBtn.setSelected(true);
                    vPaintPlayer.laserPenOn();
                }
            }
        });
        vSelectColorView = findViewById(R.id.select_color_view);
        vSelectColorBtn = (ImageView)findViewById(R.id.select_color_btn);
        vSelectColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vSelectColorView.getVisibility()==View.VISIBLE){
                    vSelectColorView.setVisibility(View.INVISIBLE);
                }else{
                    vSelectColorView.setVisibility(View.VISIBLE);
                }
            }
        });
        View.OnClickListener colorClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.select_color_black:
                        vPaintPlayer.setSelectColor(0x000000);
                        vSelectColorBtn.setBackgroundResource(R.drawable.pen_color_bg_black);
                        break;
                    case R.id.select_color_red:
                        vPaintPlayer.setSelectColor(0xcf0206);
                        vSelectColorBtn.setBackgroundResource(R.drawable.pen_color_bg_red);
                        break;
                    case R.id.select_color_yellow:
                        vPaintPlayer.setSelectColor(0xf59b00);
                        vSelectColorBtn.setBackgroundResource(R.drawable.pen_color_bg_yellow);
                        break;
                    case R.id.select_color_green:
                        vPaintPlayer.setSelectColor(0x3dc25a);
                        vSelectColorBtn.setBackgroundResource(R.drawable.pen_color_bg_green);
                        break;
                    case R.id.select_color_blue:
                        vPaintPlayer.setSelectColor(0x0029f7);
                        vSelectColorBtn.setBackgroundResource(R.drawable.pen_color_bg_blue);
                        break;
                    case R.id.select_color_purple:
                        vPaintPlayer.setSelectColor(0x8600a7);
                        vSelectColorBtn.setBackgroundResource(R.drawable.pen_color_bg_purple);
                        break;
                }
                vSelectColorView.setVisibility(View.INVISIBLE);
            }
        };
        findViewById(R.id.select_color_black).setOnClickListener(colorClickListener);
        findViewById(R.id.select_color_red).setOnClickListener(colorClickListener);
        findViewById(R.id.select_color_yellow).setOnClickListener(colorClickListener);
        findViewById(R.id.select_color_green).setOnClickListener(colorClickListener);
        findViewById(R.id.select_color_blue).setOnClickListener(colorClickListener);
        findViewById(R.id.select_color_purple).setOnClickListener(colorClickListener);
        vCameraBtn.setSelected(true);
        vMicBtn.setSelected(true);
        vCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vCameraBtn.isSelected()){
                    vCameraBtn.setSelected(false);
                    classManager.setVideoEnable(false);
                    sendChatMsg("关闭摄像头");
                    for(int i = 0;i<mPlayerList.size();i++){
                        if(mPlayerList.get(i).getUserId().equals(MLOC.userId)){
                            mPlayerList.get(i).getVideoPlayer().setVisibility(View.INVISIBLE);
                        }
                    }
                }else{
                    vCameraBtn.setSelected(true);
                    classManager.setVideoEnable(true);
                    sendChatMsg("打开摄像头");
                    for(int i = 0;i<mPlayerList.size();i++){
                        if(mPlayerList.get(i).getUserId().equals(MLOC.userId)){
                            mPlayerList.get(i).getVideoPlayer().setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
        vMicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vMicBtn.isSelected()){
                    vMicBtn.setSelected(false);
                    classManager.setAudioEnable(false);
                    sendChatMsg("关闭麦克风");
                }else{
                    vMicBtn.setSelected(true);
                    classManager.setAudioEnable(true);
                    sendChatMsg("打开麦克风");
                }
            }
        });

        vLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isUploader){
                    new AlertDialog.Builder(MiniClassActivity.this).setCancelable(true)
                            .setTitle("是否结束互动?")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                }
                            }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    isUploader = false;
                                    classManager.changeToAudience(new IXHResultCallback() {
                                        @Override
                                        public void success(Object data) {

                                        }

                                        @Override
                                        public void failed(String errMsg) {

                                        }
                                    });
                                    vLinkBtn.setText("互动");
                                    vPaintPlayer.pause();
                                    vCameraBtn.setVisibility(View.GONE);
                                    vMicBtn.setVisibility(View.GONE);
                                    vCleanBtn.setVisibility(View.GONE);
                                    vRevokeBtn.setVisibility(View.GONE);
                                }
                            }
                    ).show();
                }else{
                    new AlertDialog.Builder(MiniClassActivity.this).setCancelable(true)
                            .setTitle("是否申请互动?")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                }
                            }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    classManager.applyToBroadcaster(creatorId);
                                }
                            }
                    ).show();
                }
            }
        });

        mDatas = new ArrayList<>();
        vMsgList = (ListView) findViewById(R.id.msg_list);
        vMsgList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mAdapter = new MyChatroomListAdapter();
        vMsgList.setAdapter(mAdapter);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        init();
    }

    private void init(){
        if(creatorId.equals(MLOC.userId)){
            vLinkBtn.setVisibility(View.GONE);
            vCameraBtn.setVisibility(View.VISIBLE);
            vMicBtn.setVisibility(View.VISIBLE);
            vCleanBtn.setVisibility(View.VISIBLE);
            vRevokeBtn.setVisibility(View.VISIBLE);
            vSelectColorBtn.setVisibility(View.VISIBLE);
            vLaserPenBtn.setVisibility(View.VISIBLE);
            if(classId ==null){
                createNewClass();
            }else {
                startClass();
            }
        }else{
            vLinkBtn.setVisibility(View.VISIBLE);
            vCameraBtn.setVisibility(View.GONE);
            vMicBtn.setVisibility(View.GONE);
            vCleanBtn.setVisibility(View.GONE);
            vRevokeBtn.setVisibility(View.GONE);
            vSelectColorBtn.setVisibility(View.GONE);
            vLaserPenBtn.setVisibility(View.GONE);
            if(classId ==null){
                MLOC.showMsg(MiniClassActivity.this,"课堂ID为空");
            }else {
                joinClass();
            }
        }
    }

    private void createNewClass(){
        isUploader = true;
        //创建新直播
        XHLiveItem classItem = new XHLiveItem();
        classItem.setLiveName(className);
        classItem.setLiveType((XHConstants.XHLiveType) getIntent().getSerializableExtra(CLASS_TYPE));
        classManager.createLive(classItem, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                classId = (String) data;
                try {
                    JSONObject info = new JSONObject();
                    info.put("id",classId);
                    info.put("creator",MLOC.userId);
                    info.put("name",className);
                    String infostr = info.toString();
                    infostr = URLEncoder.encode(infostr,"utf-8");
                    if(MLOC.AEventCenterEnable){
                        InterfaceUrls.demoSaveToList(MLOC.userId,MLOC.LIST_TYPE_CLASS,classId,infostr);
                    }else{
                        classManager.saveToList(MLOC.userId,MLOC.LIST_TYPE_CLASS,classId,infostr,null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                startClass();

            }
            @Override
            public void failed(final String errMsg) {
                MLOC.showMsg(MiniClassActivity.this,errMsg);
                stopAndFinish();
            }
        });
    }

    private void stop(){
        classManager.leaveLive(new IXHResultCallback() {
            @Override
            public void success(Object data) {
                stopAndFinish();
            }

            @Override
            public void failed(final String errMsg) {
                MLOC.showMsg(MiniClassActivity.this,errMsg);
                stopAndFinish();
            }
        });
    }

    private void startClass(){
        isUploader = true;
        //开始直播
        vPaintPlayer.publish(classManager,MLOC.userId);
        classManager.startLive(classId, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                MLOC.d("XHLiveManager","startLive success "+data);
            }
            @Override
            public void failed(final String errMsg) {
                MLOC.d("XHLiveManager","startLive failed "+errMsg);
                MLOC.showMsg(MiniClassActivity.this,errMsg);
                stopAndFinish();
            }
        });
    }
    private void joinClass(){
        isUploader = false;
        //观看直播
        classManager.watchLive(classId, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                MLOC.d("XHLiveManager","watchLive success "+data);
            }
            @Override
            public void failed(final String errMsg) {
                MLOC.d("XHLiveManager","watchLive failed "+errMsg);
                MLOC.showMsg(MiniClassActivity.this,errMsg);
                stopAndFinish();
            }
        });
    }

    public void addListener(){
        AEvent.addListener(AEvent.AEVENT_LIVE_ADD_UPLOADER,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_REMOVE_UPLOADER,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_ERROR,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_GET_ONLINE_NUMBER,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_SELF_KICKED,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_SELF_BANNED,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_REV_MSG,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_REV_PRIVATE_MSG,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_REV_REALTIME_DATA,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_APPLY_LINK,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_APPLY_LINK_RESULT,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_INVITE_LINK,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_INVITE_LINK_RESULT,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_SELF_COMMANDED_TO_STOP,this);
    }
    public void removeListener(){
        AEvent.removeListener(AEvent.AEVENT_LIVE_ADD_UPLOADER,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_REMOVE_UPLOADER,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_ERROR,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_GET_ONLINE_NUMBER,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_SELF_KICKED,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_SELF_BANNED,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_REV_MSG,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_REV_PRIVATE_MSG,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_REV_REALTIME_DATA,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_APPLY_LINK,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_APPLY_LINK_RESULT,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_INVITE_LINK,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_INVITE_LINK_RESULT,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_SELF_COMMANDED_TO_STOP,this);
    }

    @Override
    public void onResume(){
        super.onResume();
        MLOC.canPickupVoip = false;
    }
    @Override
    public void onPause(){
        super.onPause();
        MLOC.canPickupVoip = true;
    }
    @Override
    public void onRestart(){
        super.onRestart();
        addListener();
    }

    @Override
    public void onStop(){
        removeListener();
        super.onStop();
    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(MiniClassActivity.this).setCancelable(true)
                .setTitle("是否退出课堂?")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        stop();
                    }
                }
        ).show();
    }

    private void addPlayer(String addUserID){
        if(mPlayerList.size()==4)return;
        ViewPosition newOne = new ViewPosition();
        newOne.setUserId(addUserID);
        StarPlayer player = new StarPlayer(this);
        newOne.setVideoPlayer(player);
        mPlayerList.add(newOne);
        vPlayerView.addView(player);
        resetLayout();
        player.setZOrderMediaOverlay(true);

        if(mPlayerList.size()==1){
            classManager.attachPlayerView(addUserID,player,true);
        }else{
            classManager.attachPlayerView(addUserID,player,false);
        }
    }

    private void deletePlayer(String removeUserId){
        if(mPlayerList!=null&&mPlayerList.size()>0){
            for(int i = 0;i<mPlayerList.size();i++) {
                ViewPosition temp = mPlayerList.get(i);
                if (temp.getUserId().equals(removeUserId)) {
                    ViewPosition remove = mPlayerList.remove(i);
                    vPlayerView.removeView(remove.getVideoPlayer());
                    resetLayout();
                    classManager.changeToBig(mPlayerList.get(0).getUserId());
                    break;
                }
            }
        }
    }

    private void resetLayout(){
        if(isPortrait){
            for(int i = 0;i<mPlayerList.size();i++){
                StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/2,borderH/2);
                player.setLayoutParams(lp);
                player.setY(i<2?0:borderH/2);
                player.setX(i%2==0?0:borderW/2);
            }
        }else{
            for(int i = 0;i<mPlayerList.size();i++){
                StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW,borderH/4);
                player.setLayoutParams(lp);
                player.setY(i*borderH/4);
                player.setX(0);
            }
        }
    }

    private String decodeMiniClassMsgContentData(String txt){
//        {
//                listType: _type,
//                from: _from,
//                fromAvatar: _fromAvatar,
//                fromNick: _fromNick,
//                text: _text
//        }

        try {
            JSONObject jsonObject = new JSONObject(txt);
            String msgTxt = "";
            msgTxt = jsonObject.getString("text");
            return msgTxt;
        } catch (JSONException e) {
            e.printStackTrace();
            return txt;
        }
    }

    private void sendChatMsg(String msg){
        MLOC.d("XHLiveManager","sendChatMsg "+msg);

//        {
//                listType: _type,
//                from: _from,
//                fromAvatar: _fromAvatar,
//                fromNick: _fromNick,
//                text: _text
//        }
        String msgTxt = msg;
        try {JSONObject jsonObject = new JSONObject();
            jsonObject.put("listType","text");
            jsonObject.put("from",MLOC.userId);
            jsonObject.put("fromAvatar","");
            jsonObject.put("fromNick",MLOC.userId);
            jsonObject.put("text",msg);
            msgTxt = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        XHIMMessage imMessage = classManager.sendMessage(msgTxt,null);
        imMessage.contentData = msg;
        mDatas.add(imMessage);
        mAdapter.notifyDataSetChanged();
    }

    public class MyChatroomListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        public MyChatroomListAdapter(){
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            if(mDatas ==null) return 0;
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            if(mDatas ==null)
                return null;
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            if(mDatas ==null)
                return 0;
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if(convertView == null){
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_class_msg_list,null);
                holder.vUserId = (TextView) convertView.findViewById(R.id.item_user_id);
                holder.vMsg = (TextView) convertView.findViewById(R.id.item_msg);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }
            String msgText = mDatas.get(position).contentData;
            holder.vMsg.setText(msgText);
            holder.vUserId.setText(mDatas.get(position).fromId);
            return convertView;
        }
    }

    public class ViewHolder{
        public TextView vUserId;
        public TextView vMsg;
    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, final Object eventObj) {
        super.dispatchEvent(aEventID,success,eventObj);
        switch (aEventID){
            case AEvent.AEVENT_LIVE_ADD_UPLOADER:
                try {
                    JSONObject data = (JSONObject) eventObj;
                    String addId = data.getString("actorID");
                    addPlayer(addId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case AEvent.AEVENT_LIVE_REMOVE_UPLOADER:
                try {
                    JSONObject data = (JSONObject) eventObj;
                    String removeUserId = data.getString("actorID");
                    deletePlayer(removeUserId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case AEvent.AEVENT_LIVE_ERROR:
                String errStr = (String) eventObj;
                MLOC.showMsg(getApplicationContext(),errStr);
                stopAndFinish();
                break;
            case AEvent.AEVENT_LIVE_GET_ONLINE_NUMBER:
                break;
            case AEvent.AEVENT_LIVE_SELF_KICKED:
                MLOC.showMsg(MiniClassActivity.this,"你已被踢出");
                stopAndFinish();
                break;
            case AEvent.AEVENT_LIVE_SELF_BANNED:
                break;
            case AEvent.AEVENT_LIVE_REV_MSG:
            case AEvent.AEVENT_LIVE_REV_PRIVATE_MSG:
                final XHIMMessage revMsg = (XHIMMessage) eventObj;
                revMsg.contentData = decodeMiniClassMsgContentData(revMsg.contentData);
                mDatas.add(revMsg);
                mAdapter.notifyDataSetChanged();
                if(revMsg.contentData.equals("打开摄像头")){
                    for(int i = 0;i<mPlayerList.size();i++){
                        if(mPlayerList.get(i).getUserId().equals(revMsg.fromId)){
                            mPlayerList.get(i).getVideoPlayer().setVisibility(View.VISIBLE);
                        }
                    }
                }else if(revMsg.contentData.equals("关闭摄像头")){
                    for(int i = 0;i<mPlayerList.size();i++){
                        if(mPlayerList.get(i).getUserId().equals(revMsg.fromId)){
                            mPlayerList.get(i).getVideoPlayer().setVisibility(View.INVISIBLE);
                        }
                    }
                }
                break;
            case AEvent.AEVENT_LIVE_REV_REALTIME_DATA:
                if(success){
                    try {
                        JSONObject jsonObject = (JSONObject) eventObj;
                        byte[] tData = (byte[]) jsonObject.get("data");
                        String tUpid = jsonObject.getString("upId");
                        vPaintPlayer.setPaintData(tData,tUpid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case AEvent.AEVENT_LIVE_APPLY_LINK:
                new AlertDialog.Builder(MiniClassActivity.this).setCancelable(true)
                        .setTitle(eventObj+"申请互动")
                        .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                classManager.refuseApplyToBroadcaster((String) eventObj);
                            }
                        }).setPositiveButton("同意", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                sendChatMsg("欢迎新的小伙伴上麦！！！");
                                classManager.agreeApplyToBroadcaster((String) eventObj);
                            }
                        }
                ).show();
                break;
            case AEvent.AEVENT_LIVE_APPLY_LINK_RESULT:
                isUploader = true;
                classManager.changeToBroadcaster(new IXHResultCallback() {
                    @Override
                    public void success(Object data) {

                    }

                    @Override
                    public void failed(String errMsg) {

                    }
                });
                vPaintPlayer.publish(classManager,MLOC.userId);
                vLinkBtn.setText("停止");
                vCameraBtn.setVisibility(View.VISIBLE);
                vMicBtn.setVisibility(View.VISIBLE);
                vCleanBtn.setVisibility(View.GONE);
                vRevokeBtn.setVisibility(View.VISIBLE);
                vSelectColorBtn.setVisibility(View.VISIBLE);
                vLaserPenBtn.setVisibility(View.GONE);
                break;
            case AEvent.AEVENT_LIVE_INVITE_LINK:
                new AlertDialog.Builder(MiniClassActivity.this).setCancelable(true)
                        .setTitle(eventObj+"邀请您互动")
                        .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                classManager.refuseInviteToBroadcaster((String) eventObj);
                            }
                        }).setPositiveButton("同意", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                vLinkBtn.setText("停止");
                                vCameraBtn.setVisibility(View.VISIBLE);
                                vMicBtn.setVisibility(View.VISIBLE);
                                vCleanBtn.setVisibility(View.GONE);
                                vRevokeBtn.setVisibility(View.VISIBLE);
                                vSelectColorBtn.setVisibility(View.VISIBLE);
                                vLaserPenBtn.setVisibility(View.GONE);
                                isUploader = true;
                                classManager.agreeInviteToBroadcaster((String) eventObj);
                            }
                        }
                ).show();
                break;
            case AEvent.AEVENT_LIVE_INVITE_LINK_RESULT:
                XHConstants.XHLiveJoinResult result = (XHConstants.XHLiveJoinResult) eventObj;
                switch (result){
                    case XHLiveJoinResult_accept:
                        vLinkBtn.setText("停止");
                        vCameraBtn.setVisibility(View.VISIBLE);
                        vMicBtn.setVisibility(View.VISIBLE);
                        vCleanBtn.setVisibility(View.GONE);
                        vRevokeBtn.setVisibility(View.VISIBLE);
                        vSelectColorBtn.setVisibility(View.VISIBLE);
                        vLaserPenBtn.setVisibility(View.GONE);
                        break;
                    case XHLiveJoinResult_refuse:
                        break;
                    case XHLiveJoinResult_outtime:
                        break;
                }
                break;
            case AEvent.AEVENT_LIVE_SELF_COMMANDED_TO_STOP:
                if(isUploader){
                    isUploader = false;
                    vPaintPlayer.pause();
                    vLinkBtn.setText("互动");
                    vCameraBtn.setVisibility(View.GONE);
                    vMicBtn.setVisibility(View.GONE);
                    vCleanBtn.setVisibility(View.GONE);
                    vRevokeBtn.setVisibility(View.GONE);
                    vSelectColorBtn.setVisibility(View.GONE);
                    vLaserPenBtn.setVisibility(View.GONE);
                }
                break;
        }
    }
    private void stopAndFinish(){
        if(starRTCAudioManager!=null){
            starRTCAudioManager.stop();
        }
        vPaintPlayer.pause();
        removeListener();
        finish();
    }
}
