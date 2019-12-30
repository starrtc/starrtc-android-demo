package com.starrtc.demo.demo.superroom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.demo.demo.MLOC;
import com.starrtc.demo.listener.XHSuperRoomManagerListener;
import com.starrtc.demo.serverAPI.InterfaceUrls;
import com.starrtc.demo.utils.AEvent;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.api.XHConstants;
import com.starrtc.starrtcsdk.api.XHSuperRoomItem;
import com.starrtc.starrtcsdk.api.XHSuperRoomManager;
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback;
import com.starrtc.starrtcsdk.core.audio.StarRTCAudioManager;
import com.starrtc.starrtcsdk.core.im.message.XHIMMessage;
import com.starrtc.starrtcsdk.core.pusher.XHCameraRecorder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SuperRoomActivity extends BaseActivity {

    public static String CREATER_ID         = "CREATER_ID";          //创建者ID
    public static String LIVE_TYPE          = "LIVE_TYPE";           //创建信息
    public static String LIVE_ID            = "LIVE_ID";            //直播ID
    public static String LIVE_NAME          = "LIVE_NAME";          //直播名称

    private TextView vRoomId;
    private TextView vOnlineNum;
    private ListView vMsgList;
    private View vSendBtn;
    private EditText vEditText;
    private View vPushBtn;
    private View vChatBtn;
    private View vAudioBtn;

    private int onLineUserNumber = 0;

    private List<String> mPlayerList;
    private List<XHIMMessage> mDatas;
    private MyChatroomListAdapter mAdapter ;

    private String mPrivateMsgTargetId;
    private XHSuperRoomManager superRoomManager;
    private String createrId;
    private String liveId;
    private String liveName;
    private XHConstants.XHSuperRoomType roomType;

    private ArrayList<TextView> vNameArray;
    private ArrayList<ImageView> vHeadArray;
    private String[] mNameArray = new String[]{"","","","","","",""};

    private StarRTCAudioManager starRTCAudioManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MLOC.d("liveVideoVdn","+==================================================");
        MLOC.d("liveVideoVdn","+==================================================");
        MLOC.d("liveVideoVdn","+==================================================");
        MLOC.d("liveVideoVdn","+==================================================");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_super_room);

        starRTCAudioManager = StarRTCAudioManager.create(this);
        starRTCAudioManager.start(new StarRTCAudioManager.AudioManagerEvents() {
            @Override
            public void onAudioDeviceChanged(StarRTCAudioManager.AudioDevice selectedAudioDevice, Set availableAudioDevices) {

            }
        });

        createrId = getIntent().getStringExtra(CREATER_ID);
        liveName = getIntent().getStringExtra(LIVE_NAME);
        liveId = getIntent().getStringExtra(LIVE_ID);
        roomType = (XHConstants.XHSuperRoomType) getIntent().getSerializableExtra(LIVE_TYPE);

        if(TextUtils.isEmpty(liveId)){
            if(createrId.equals(MLOC.userId)){
                if(TextUtils.isEmpty(liveName)||roomType==null){
                    MLOC.showMsg(this,"没有直播信息");
                    stopAndFinish();
                    return;
                }
            }else{
                if(TextUtils.isEmpty(liveName)||roomType==null){
                    MLOC.showMsg(this,"没有直播信息");
                    stopAndFinish();
                    return;
                }
            }
        }

        vNameArray = new ArrayList<>();
        vNameArray.add((TextView) findViewById(R.id.mc_id));
        vNameArray.add((TextView) findViewById(R.id.mc_id_1));
        vNameArray.add((TextView) findViewById(R.id.mc_id_2));
        vNameArray.add((TextView) findViewById(R.id.mc_id_3));
        vNameArray.add((TextView) findViewById(R.id.mc_id_4));
        vNameArray.add((TextView) findViewById(R.id.mc_id_5));
        vNameArray.add((TextView) findViewById(R.id.mc_id_6));
        vHeadArray = new ArrayList<>();
        vHeadArray.add((ImageView) findViewById(R.id.mic_head));
        vHeadArray.add((ImageView) findViewById(R.id.mc_head_1));
        vHeadArray.add((ImageView) findViewById(R.id.mc_head_2));
        vHeadArray.add((ImageView) findViewById(R.id.mc_head_3));
        vHeadArray.add((ImageView) findViewById(R.id.mc_head_4));
        vHeadArray.add((ImageView) findViewById(R.id.mc_head_5));
        vHeadArray.add((ImageView) findViewById(R.id.mc_head_6));

        superRoomManager = XHClient.getInstance().getSuperRoomManager(this);
        superRoomManager.addListener(new XHSuperRoomManagerListener());
        superRoomManager.setRtcMediaType(XHConstants.XHRtcMediaTypeEnum.STAR_RTC_MEDIA_TYPE_AUDIO_ONLY);
        superRoomManager.setRecorder(new XHCameraRecorder());


        addListener();
        vRoomId = (TextView) findViewById(R.id.live_id_text);
        vRoomId.setText("房间名称："+ liveName);

        vOnlineNum = findViewById(R.id.online_text);
        vOnlineNum.setText(onLineUserNumber+" 人在线");

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        vEditText = (EditText) findViewById(R.id.id_input);
        vEditText.clearFocus();
        mDatas = new ArrayList<>();
        vMsgList = (ListView) findViewById(R.id.msg_list);

        vMsgList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        vMsgList.setStackFromBottom(true);

        mAdapter = new MyChatroomListAdapter();
        vMsgList.setAdapter(mAdapter);
        vMsgList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickUserId = mDatas.get(position).fromId;
                String msgText = mDatas.get(position).contentData;
                showManagerDialog(clickUserId,msgText);
            }
        });

        vSendBtn = findViewById(R.id.send_btn);
        vSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = vEditText.getText().toString();
                if(!TextUtils.isEmpty(txt)){
                    sendChatMsg(txt);
                    vEditText.setText("");
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(vEditText.getWindowToken(), 0);
            }
        });

        vPushBtn = findViewById(R.id.push_btn);
        vAudioBtn = findViewById(R.id.audio_btn);
        vChatBtn = findViewById(R.id.chat_btn);

        vAudioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.audio_container).setVisibility(View.VISIBLE);
                findViewById(R.id.chat_container).setVisibility(View.GONE);
            }
        });
        vChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.audio_container).setVisibility(View.GONE);
                findViewById(R.id.chat_container).setVisibility(View.VISIBLE);
            }
        });

        vPushBtn.setOnTouchListener(new View.OnTouchListener() {
            int lastAction = -111;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(lastAction!=event.getAction()){
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            vPushBtn.setSelected(true);
                            superRoomManager.pickUpMic(new IXHResultCallback() {
                                @Override
                                public void success(Object data) {
//                                    MLOC.showMsg(SuperRoomActivity.this,"可以发言了");
                                }

                                @Override
                                public void failed(String errMsg) {
//                                    MLOC.showMsg(SuperRoomActivity.this,"发言failed");
                                }
                            });
                            break;
                        case MotionEvent.ACTION_OUTSIDE:
                        case MotionEvent.ACTION_UP:
                            vPushBtn.setSelected(false);
                            superRoomManager.layDownMic(new IXHResultCallback() {
                                @Override
                                public void success(Object data) {
//                                    MLOC.showMsg(SuperRoomActivity.this,"已经交出发言权限");
                                }

                                @Override
                                public void failed(String errMsg) {
//                                    MLOC.showMsg(SuperRoomActivity.this,"交出发言权限failed");
                                }
                            });
                            break;
                    }
                    lastAction = event.getAction();
                }
                return true;
            }
        });
        init();
    }

    private void init(){
        findViewById(R.id.audio_container).setVisibility(View.VISIBLE);
        findViewById(R.id.chat_container).setVisibility(View.GONE);
        if(createrId.equals(MLOC.userId)){
            if(liveId==null){
                createNewLive();
            }else {
                joinLive();
            }
        }else{
            joinLive();
        }
    }

    private void createNewLive(){
        //创建新直播
        XHSuperRoomItem liveItem = new XHSuperRoomItem();
        liveItem.setSuperRoomName(liveName);
        liveItem.setSuperRoomType(roomType);
        superRoomManager.createSuperRoom(liveItem, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                liveId = (String) data;
                MLOC.showMsg(SuperRoomActivity.this,"创建成功");
                try {
                    JSONObject info = new JSONObject();
                    info.put("id",liveId);
                    info.put("creator",MLOC.userId);
                    info.put("name",liveName);
                    String infostr = info.toString();
                    infostr = URLEncoder.encode(infostr,"utf-8");
                    if(MLOC.AEventCenterEnable){
                        InterfaceUrls.demoSaveToList(MLOC.userId,MLOC.LIST_TYPE_SUPER_ROOM,liveId,infostr);
                    }else{
                        superRoomManager.saveToList(MLOC.userId,MLOC.LIST_TYPE_SUPER_ROOM,liveId,infostr,null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                joinLive();
            }
            @Override
            public void failed(final String errMsg) {
                MLOC.showMsg(SuperRoomActivity.this,errMsg);
                stopAndFinish();
            }
        });
    }

    private void joinLive(){
        //观众加入直播
        superRoomManager.joinSuperRoom(liveId, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                MLOC.showMsg(SuperRoomActivity.this,"按住下方按钮发言");
                MLOC.d("XHLiveManager","watchLive success "+data);
            }
            @Override
            public void failed(final String errMsg) {
                MLOC.d("XHLiveManager","watchLive failed "+errMsg);
                MLOC.showMsg(SuperRoomActivity.this,errMsg);
                stopAndFinish();
            }
        });
    }

    private void sendChatMsg(String msg){
        MLOC.d("XHLiveManager","sendChatMsg "+msg);
        if(TextUtils.isEmpty(mPrivateMsgTargetId)){
            XHIMMessage imMessage = superRoomManager.sendMessage(msg,null);
            mDatas.add(imMessage);
        }else{
            XHIMMessage imMessage = superRoomManager.sendPrivateMessage(msg,mPrivateMsgTargetId,null);
            mDatas.add(imMessage);
        }
        mAdapter.notifyDataSetChanged();
        mPrivateMsgTargetId = "";
    }

    public void addListener(){
        AEvent.addListener(AEvent.AEVENT_SUPER_ROOM_ERROR,this);
        AEvent.addListener(AEvent.AEVENT_SUPER_ROOM_ADD_UPLOADER,this);
        AEvent.addListener(AEvent.AEVENT_SUPER_ROOM_REMOVE_UPLOADER,this);
        AEvent.addListener(AEvent.AEVENT_SUPER_ROOM_GET_ONLINE_NUMBER,this);
        AEvent.addListener(AEvent.AEVENT_SUPER_ROOM_SELF_KICKED,this);
        AEvent.addListener(AEvent.AEVENT_SUPER_ROOM_SELF_BANNED,this);
        AEvent.addListener(AEvent.AEVENT_SUPER_ROOM_REV_MSG,this);
        AEvent.addListener(AEvent.AEVENT_SUPER_ROOM_REV_PRIVATE_MSG,this);
        AEvent.addListener(AEvent.AEVENT_SUPER_ROOM_SELF_COMMANDED_TO_STOP,this);
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

    private void removeListener(){
        AEvent.removeListener(AEvent.AEVENT_SUPER_ROOM_ERROR,this);
        AEvent.removeListener(AEvent.AEVENT_SUPER_ROOM_ADD_UPLOADER,this);
        AEvent.removeListener(AEvent.AEVENT_SUPER_ROOM_REMOVE_UPLOADER,this);
        AEvent.removeListener(AEvent.AEVENT_SUPER_ROOM_GET_ONLINE_NUMBER,this);
        AEvent.removeListener(AEvent.AEVENT_SUPER_ROOM_SELF_KICKED,this);
        AEvent.removeListener(AEvent.AEVENT_SUPER_ROOM_SELF_BANNED,this);
        AEvent.removeListener(AEvent.AEVENT_SUPER_ROOM_REV_MSG,this);
        AEvent.removeListener(AEvent.AEVENT_SUPER_ROOM_REV_PRIVATE_MSG,this);
        AEvent.removeListener(AEvent.AEVENT_SUPER_ROOM_SELF_COMMANDED_TO_STOP,this);
    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(SuperRoomActivity.this).setCancelable(true)
                .setTitle("是否要退出?")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        stopAndFinish();
                    }
                }
        ).show();
    }

    private void addPlayer(String addUserID){
        if(mPlayerList==null){
            mPlayerList = new ArrayList<>();
        }
        for(int i = 0;i<mPlayerList.size();i++){
            if(mPlayerList.get(i).equals(addUserID)){
                return;
            }
        }

        mPlayerList.add(addUserID);
        for(int i = 0;i<mNameArray.length;i++){
            if(mNameArray[i].isEmpty()){
                mNameArray[i] = addUserID;
                vHeadArray.get(i).setImageResource(MLOC.getHeadImage(this,addUserID));
                break;
            }
        }
        for(int i = 0;i<vNameArray.size();i++){
            vNameArray.get(i).setText(mNameArray[i]);
            if(mNameArray[i].isEmpty()){
                vHeadArray.get(i).setImageDrawable(null);
            }
        }
    }

    private void deletePlayer(String removeUserId){
        if(mPlayerList==null){
            mPlayerList = new ArrayList<>();
        }
        for(int i = 0;i<mPlayerList.size();i++){
            if(mPlayerList.get(i).equals(removeUserId)){
                mPlayerList.remove(i);
            }
        }
        for(int i = 0;i<mNameArray.length;i++){
            if(mNameArray[i].equals(removeUserId)){
                mNameArray[i] = "";
            }
        }
        for(int i = 0;i<vNameArray.size();i++){
            vNameArray.get(i).setText(mNameArray[i]);
            if(mNameArray[i].isEmpty()){
                vHeadArray.get(i).setImageDrawable(null);
            }
        }
    }



    @Override
    public void dispatchEvent(String aEventID, boolean success, final Object eventObj) {
        super.dispatchEvent(aEventID,success,eventObj);
        MLOC.d("SuperRoomActivity","dispatchEvent  "+aEventID + eventObj);
        switch (aEventID){
            case AEvent.AEVENT_SUPER_ROOM_ADD_UPLOADER:
                try {
                    JSONObject data = (JSONObject) eventObj;
                    String addId = data.getString("actorID");
                    addPlayer(addId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case AEvent.AEVENT_SUPER_ROOM_REMOVE_UPLOADER:
                try {
                    JSONObject data = (JSONObject) eventObj;
                    String removeUserId = data.getString("actorID");
                    deletePlayer(removeUserId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case AEvent.AEVENT_SUPER_ROOM_GET_ONLINE_NUMBER:
                onLineUserNumber = (int) eventObj;
                vOnlineNum.setText(onLineUserNumber+" 人在线");
                break;
            case AEvent.AEVENT_SUPER_ROOM_SELF_KICKED:
                MLOC.showMsg(SuperRoomActivity.this,"你已被踢出");
                stopAndFinish();
                break;
            case AEvent.AEVENT_SUPER_ROOM_SELF_BANNED:
                final String banTime = eventObj.toString();
                MLOC.showMsg(SuperRoomActivity.this,"你已被禁言,"+banTime+"秒后自动解除");
                break;
            case AEvent.AEVENT_SUPER_ROOM_REV_MSG:
                XHIMMessage revMsg = (XHIMMessage) eventObj;
                mDatas.add(revMsg);
                mAdapter.notifyDataSetChanged();
                break;
            case AEvent.AEVENT_SUPER_ROOM_REV_PRIVATE_MSG:
                XHIMMessage revMsgPrivate = (XHIMMessage) eventObj;
                mDatas.add(revMsgPrivate);
                mAdapter.notifyDataSetChanged();
                break;
            case AEvent.AEVENT_SUPER_ROOM_ERROR:
                String errStr = (String) eventObj;
                MLOC.showMsg(getApplicationContext(),errStr);
                if(errStr.equals("ERROR_VDN_DISCONNECTED")){
                    superRoomManager.leaveSuperRoom(new IXHResultCallback() {
                        @Override
                        public void success(Object data) {
                            MLOC.d("SuperRoomActivity","leaveSuperRoom  success");
                            joinLive();
                        }
                        @Override
                        public void failed(String errMsg) {
                            MLOC.d("SuperRoomActivity","leaveSuperRoom  failed");
                        }
                    });
                }else if(errStr.equals("ERROR_SRC_DISCONNECTED")){

                }else {
                    stopAndFinish();
                }
                break;
            case AEvent.AEVENT_SUPER_ROOM_SELF_COMMANDED_TO_STOP:
                vAudioBtn.setVisibility(View.GONE);
                findViewById(R.id.audio_container).setVisibility(View.GONE);
                findViewById(R.id.chat_container).setVisibility(View.VISIBLE);
                MLOC.showMsg(SuperRoomActivity.this,"你被叫停");
                break;
        }
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
                convertView = mInflater.inflate(R.layout.item_live_msg_list,null);
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


    private void showManagerDialog(final String userId,final String msgText) {
        if(!userId.equals(MLOC.userId)){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            if(createrId.equals(MLOC.userId)){
                Boolean ac = false;
                if(mPlayerList!=null){
                    for(int i = 0 ;i<mPlayerList.size();i++){
                        if(userId.equals(mPlayerList.get(i))){
                            ac = true;
                            break;
                        }
                    }
                }
                if(ac){
                    final String[] Items={"踢出房间","禁止发言","私信","下麦"};
                    builder.setItems(Items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(i==0){
                                kickUser(userId);
                            }else if(i==1){
                                muteUser(userId,60);
                            }else if(i==2){
                                mPrivateMsgTargetId = userId;
                                vEditText.setText("[私"+userId+"]");
                            }else if(i==3){
                                superRoomManager.commandToAudience(userId);
                            }
                        }
                    });
                }else{
                    final String[] Items={"踢出房间","禁止发言","私信","邀请上麦"};
                    builder.setItems(Items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(i==0){
                                kickUser(userId);
                            }else if(i==1){
                                muteUser(userId,60);
                            }else if(i==2){
                                mPrivateMsgTargetId = userId;
                                vEditText.setText("[私"+userId+"]");
                            }
                        }
                    });
                }


            }else{
                final String[] Items={"私信"};
                builder.setItems(Items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            mPrivateMsgTargetId = userId;
                            vEditText.setText("[私"+userId+"]");
                        }
                    }
                });
            }
            builder.setCancelable(true);
            AlertDialog dialog=builder.create();
            dialog.show();
        }

    }

    private void kickUser(String userId){
        superRoomManager.kickMember(userId, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                //踢人成功
                MLOC.showMsg(SuperRoomActivity.this,"踢人成功");
            }

            @Override
            public void failed(String errMsg) {
                //踢人失败
                MLOC.showMsg(SuperRoomActivity.this,"踢人失败");
            }
        });
    }
    private void muteUser(String userId,int times){
        superRoomManager.muteMember(userId, times, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                //禁言成功
                MLOC.showMsg(SuperRoomActivity.this,"禁言成功");
            }

            @Override
            public void failed(String errMsg) {
                //禁言失败
                MLOC.showMsg(SuperRoomActivity.this,"禁言失败");
            }
        });
    }

    private void stopAndFinish(){
        starRTCAudioManager.stop();
        superRoomManager.leaveSuperRoom(new IXHResultCallback() {
            @Override
            public void success(Object data) {
                MLOC.d("SuperRoomActivity","leaveSuperRoom  success");
                finish();
            }
            @Override
            public void failed(String errMsg) {
                MLOC.d("SuperRoomActivity","leaveSuperRoom  failed");
                finish();
            }
        });
        removeListener();
    }
}
