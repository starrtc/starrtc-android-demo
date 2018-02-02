package com.starrtc.staravdemo.demo.videolive;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.starrtc.staravdemo.R;
import com.starrtc.staravdemo.demo.MLOC;
import com.starrtc.staravdemo.demo.serverAPI.InterfaceUrls;
import com.starrtc.staravdemo.demo.videomeeting.ViewPosition;
import com.starrtc.staravdemo.utils.AEvent;
import com.starrtc.staravdemo.utils.IEventListener;
import com.starrtc.starrtcsdk.StarManager;
import com.starrtc.starrtcsdk.im.message.StarIMMessage;
import com.starrtc.starrtcsdk.im.message.StarIMMessageBuilder;
import com.starrtc.starrtcsdk.live.StarLiveConfig;
import com.starrtc.starrtcsdk.live.StarLiveVideoSizeConfigType;
import com.starrtc.starrtcsdk.player.StarPlayer;
import com.starrtc.starrtcsdk.player.StarPlayerScaleType;
import com.starrtc.starrtcsdk.utils.StarLog;
import com.starrtc.starrtcsdk.utils.StringUtils;

public class VideoLiveActivity extends Activity implements IEventListener {

    private LinearLayout vLine1,vLine2;
    private List<ViewPosition> mViewList;

    public static String LIVE_ID            = "LIVE_ID";            //直播ID
    public static String CHANNEL_ID         = "CHANNEL_ID";         //频道ID
    public static String CHATROOM_ID        = "CHATROOM_ID";         //聊天室ID
    public static String CREATER_ID         = "CREATER_ID";         //创建者ID

    private TextView vRoomId;
    private ListView vMsgList;
    private View vSendBtn;
    private EditText vEditText;


    private String liveId;
    private String channelId;
    private String chatroomId;
    private String createrId;
    private String mPrivateMsgTargetId;

    private int maxMessageLength;
    private int onLineUserNumber;

    private boolean joinChatroomOk = false;
    private boolean joinLiveOk = false;

    private List<StarIMMessage> mDatas;
    private MyChatroomListAdapter mAdapter ;

    private int bigUpid;
    private ViewPosition bigViewPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_live);

        liveId = getIntent().getStringExtra(LIVE_ID);
        channelId = getIntent().getStringExtra(CHANNEL_ID);
        chatroomId = getIntent().getStringExtra(CHATROOM_ID);
        createrId = getIntent().getStringExtra(CREATER_ID);

        if(StringUtils.isEmpty(liveId)){
            liveId = "live_"+ liveId;
        }

        vRoomId = (TextView) findViewById(R.id.live_id_text);
        vRoomId.setText("直播编号："+ liveId);

        vLine1 = (LinearLayout) findViewById(R.id.line1);
        vLine2 = (LinearLayout) findViewById(R.id.line2);

        bigViewPosition = addNewPlayer();
        bigViewPosition.setUserId(MLOC.agentId+"_"+MLOC.userId);
        bigViewPosition.setUpId(-1);
        bigViewPosition.setBigW(480);
        bigViewPosition.setBigH(640);
        bigViewPosition.setSmallW(480);
        bigViewPosition.setSmallH(640);
        bigUpid = -1;
        StarLog.d("@@@@@initLive","");
        StarManager.getInstance().initLive(getApplicationContext(), bigViewPosition.getVideoPlayer());

        /*if(createrId.equals(MLOC.userId)){
            bigViewPosition = addNewPlayer();
            bigViewPosition.setUserId(MLOC.agentId+"_"+MLOC.userId);
            bigViewPosition.setUpId(-1);
            bigViewPosition.setBigW(480);
            bigViewPosition.setBigH(640);
            bigViewPosition.setSmallW(480);
            bigViewPosition.setSmallH(640);
            bigUpid = -1;
            StarLog.d("@@@@@initLive","");
            StarManager.getInstance().initLive(getApplicationContext(), bigViewPosition.getVideoPlayer());
        }else{
            IStarCallback callback = new IStarCallback() {
                @Override
                public void callback(boolean reqSuccess, String statusCode, String data) {
                    if(reqSuccess){
                        StarLog.d("@@@@@VideoLiveActivity","申请观看成功");
                        if(StringUtils.isNotEmpty(chatroomId)){
                            StarLog.d("@@@@@VideoLiveActivity","加入聊天室："+chatroomId);
                            StarManager.getInstance().joinChatroom(chatroomId);
                        }else{
                            StarLog.d("@@@@@VideoLiveActivity","聊天室ID为空");
                        }
                    }else{
                        StarLog.d("@@@@@VideoLiveActivity","申请观看失败");
                    }
                }
            };
            StarLog.d("@@@@@joinLive","channelId="+channelId);
            StarManager.getInstance().joinLive(channelId, callback);
        }*/

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        vEditText = (EditText) findViewById(R.id.id_input);
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
                showManagerDialog(clickUserId);
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
            }
        });

    }
    private void sendChatMsg(String msg){
        StarIMMessage imMessage = StarIMMessageBuilder.getGhatRoomMessage(MLOC.userId,chatroomId,msg);
        if(TextUtils.isEmpty(mPrivateMsgTargetId)){
            StarManager.getInstance().sendChatroomMessage(imMessage);
        }else{
            StarManager.getInstance().sendChatroomPrivateMessage(mPrivateMsgTargetId,imMessage);
        }
        mDatas.add(imMessage);
        mAdapter.notifyDataSetChanged();
        mPrivateMsgTargetId = "";
    }

    @Override
    public void onStart(){
        super.onStart();
        AEvent.addListener(AEvent.AEVENT_LIVE_INIT_COMPLETE,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_JOIN_OK,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_ADD_UPLOADER,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_REMOVE_UPLOADER,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_RESIZE_ALL_VIDEO,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_ERROR,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_STOP_OK,this);

        AEvent.addListener(AEvent.AEVENT_CHATROOM_CREATE_OK,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_CREATE_FAILED,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_JOIN_OK,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_JOIN_FAILED,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_REV_MSG,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_REV_PRIVATE_MSG,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_GET_ONLINE_NUMBER,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_ERROR,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_SELF_KICKED,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_SELF_BANNED,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_KICK_OUT_OK,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_KICK_OUT_FAILED,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_BAN_USER_OK,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_BAN_USER_FAILED,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_STOP_OK,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_DELETE_OK,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_SEND_MSG_SUCCESS,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_SEND_MSG_FAILED,this);
    }

    @Override
    public void onPause(){
        super.onPause();
        StarManager.getInstance().toBackground();
    }

    @Override
    public void onResume(){
        super.onResume();
        StarManager.getInstance().toForground();
    }

    @Override
    public void onStop(){
        AEvent.removeListener(AEvent.AEVENT_LIVE_INIT_COMPLETE,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_JOIN_OK,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_ADD_UPLOADER,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_REMOVE_UPLOADER,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_RESIZE_ALL_VIDEO,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_ERROR,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_STOP_OK,this);

        AEvent.removeListener(AEvent.AEVENT_CHATROOM_CREATE_OK,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_CREATE_FAILED,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_JOIN_OK,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_JOIN_FAILED,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_REV_MSG,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_REV_PRIVATE_MSG,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_GET_ONLINE_NUMBER,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_ERROR,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_SELF_KICKED,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_SELF_BANNED,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_KICK_OUT_OK,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_KICK_OUT_FAILED,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_BAN_USER_OK,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_BAN_USER_FAILED,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_STOP_OK,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_DELETE_OK,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_SEND_MSG_SUCCESS,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_SEND_MSG_FAILED,this);
        super.onStop();
    }

    @Override
    public void onBackPressed(){
        StarManager.getInstance().stopLive();
        StarManager.getInstance().exitChatroom();
    }


    private ViewPosition addNewPlayer(){
        if(mViewList==null){
            mViewList = new ArrayList<ViewPosition>();
        }
        try {
            if(mViewList.size()<7){
                final StarPlayer tv = new StarPlayer(this);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetView(v);
                    }
                });
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                int[] pos = new int[]{R.id.view1,R.id.view2,R.id.view3,R.id.view4,R.id.view5,R.id.view6,R.id.view7};
                for (int id:pos){
                    RelativeLayout pv = (RelativeLayout) findViewById(id);
                    boolean used = false;
                    for(ViewPosition v:mViewList){
                        if(v.getParentView()==pv){
                            used = true;
                            break;
                        }
                    }
                    if(!used){
                        ViewPosition viewPosition = new ViewPosition();
                        viewPosition.setParentView(pv);
                        viewPosition.setVideoPlayer(tv);
                        mViewList.add(viewPosition);
                        pv.addView(tv,lp);
                        return viewPosition;
                    }
                }
                return null;
            }
            return null;
        }finally {
            if(mViewList.size()==1){
                vLine1.setVisibility(View.GONE);
                vLine2.setVisibility(View.GONE);
            }else if(mViewList.size()>1&&mViewList.size()<=4){
                vLine1.setVisibility(View.VISIBLE);
                vLine2.setVisibility(View.GONE);
            }else if(mViewList.size()>4&&mViewList.size()<=7){
                vLine1.setVisibility(View.VISIBLE);
                vLine2.setVisibility(View.VISIBLE);
            }
            resizeAllVideo();
        }

    }

    private void removeLeavePlayer(String removeUserId){
        try {
            for(int i = 0;i<mViewList.size();i++){
                ViewPosition temp = mViewList.get(i);
                if(temp.getUserId().equals(removeUserId)){
                    StarManager.getInstance().meetingRemoveUser(temp.getUserId(),temp.getUpId());
                    ViewPosition last = mViewList.get(mViewList.size()-1);
                    last.getParentView().removeView(last.getVideoPlayer());
                    for(int j = i+1;j<mViewList.size();j++){
                        ViewPosition nextVp = mViewList.get(j);
                        ViewPosition thisVp = mViewList.get(j-1);
                        thisVp.setUpId(nextVp.getUpId());
                        thisVp.setUserId(nextVp.getUserId());
                        if(i==0){
                            //删除的是大图
                            bigUpid = thisVp.getUpId();
                            StarManager.getInstance().setBigVideo(thisVp.getUserId(),thisVp.getUpId());
                        }
                        if(nextVp.getUpId()==-1){
                            StarManager.getInstance().setPreviewVideoView(1,thisVp.getVideoPlayer());
                        }else{
                            StarManager.getInstance().setVideoView(thisVp.getUpId(),thisVp.getVideoPlayer());
                        }

                    }
                    mViewList.remove(mViewList.size()-1);
                    break;
                }
            }
        }finally {
            if(mViewList.size()==1){
                vLine1.setVisibility(View.GONE);
                vLine2.setVisibility(View.GONE);
            }else if(mViewList.size()>1&&mViewList.size()<=4){
                vLine1.setVisibility(View.VISIBLE);
                vLine2.setVisibility(View.GONE);
            }else if(mViewList.size()>4&&mViewList.size()<=7){
                vLine1.setVisibility(View.VISIBLE);
                vLine2.setVisibility(View.VISIBLE);
            }
            resizeAllVideo();
        }
    }

    private void resizeAllVideo(){
        uiHandler.sendEmptyMessageDelayed(2,100);
    }

    private void resetView(View v){
        ViewPosition click = null;
        for(int i=0;i<mViewList.size();i++){
            ViewPosition viewPosition  = mViewList.get(i);
            if(v == viewPosition.getVideoPlayer()){
                click = viewPosition;
                bigUpid = viewPosition.getUpId();
                StarManager.getInstance().setBigVideo(viewPosition.getUserId(),viewPosition.getUpId());
            }
            if(viewPosition.getParentView()==findViewById(R.id.view1)){
                bigViewPosition = viewPosition;
            }
        }
        int clickId = click.getUpId();
        int mainId = bigViewPosition.getUpId();
        StarLog.d("meeting@@","clickId"+clickId);
        StarLog.d("meeting@@","mainId"+mainId);
        if(clickId==-1){
            StarManager.getInstance().setPreviewVideoView(1,bigViewPosition.getVideoPlayer());
        }else{
            StarManager.getInstance().setVideoView(clickId,bigViewPosition.getVideoPlayer());
        }
        if(mainId==-1){
            StarManager.getInstance().setPreviewVideoView(1, click.getVideoPlayer());
        }else{
            StarManager.getInstance().setVideoView(mainId, click.getVideoPlayer());
        }

        int tid = click.getUpId();
        String tUid = click.getUserId();
        click.setUpId(bigViewPosition.getUpId());
        click.setUserId(bigViewPosition.getUserId());
        bigViewPosition.setUpId(tid);
        bigViewPosition.setUserId(tUid);
        click.getVideoPlayer().setZOrderMediaOverlay(true);
        bigViewPosition.getVideoPlayer().setZOrderMediaOverlay(false);
//        resizeAllVideo();

    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, Object eventObj) {
        switch (aEventID){
            case AEvent.AEVENT_LIVE_INIT_COMPLETE:
                if(success){

                    if(StringUtils.isEmpty(createrId)){
                        createrId = MLOC.userId;
                    }

                    StarManager.getInstance().setPreviewVideoView(1,bigViewPosition.getVideoPlayer());

                    if(StringUtils.isNotEmpty(channelId)){
                        StarLog.d("@@@@@VideoLiveActivity","使用已存在频道上传视频："+channelId);
                        StarManager.getInstance().applyStartMeeting( new StarLiveConfig(),liveId,channelId);
                    }else{
                        StarLog.d("@@@@@VideoLiveActivity","申请新的会议频道上传视频");
                        StarManager.getInstance().applyStartNewMeeting(new StarLiveConfig(),liveId,7);
                    }

                    if(StringUtils.isNotEmpty(chatroomId)){
                        StarLog.d("@@@@@VideoLiveActivity","加入聊天室："+chatroomId);
                        StarManager.getInstance().joinChatroom(chatroomId);
                    }else{
                        StarLog.d("@@@@@VideoLiveActivity","创建聊天室");
                        StarManager.getInstance().createChatroom(liveId);
                    }

                }else{
                    MLOC.showMsg("initEncoder ERROR!!!");
                    finish();
                }
                break;
            case AEvent.AEVENT_LIVE_JOIN_OK:
                joinLiveOk = true;
                channelId = (String) eventObj;
                reportCP();
                break;
            case AEvent.AEVENT_LIVE_ADD_UPLOADER:
                Message msg = new Message();
                msg.what = 0;
                Bundle b = new Bundle();
                b.putString("data",((JSONObject)eventObj).toString());
                msg.setData(b);
                uiHandler.sendMessage(msg);
                break;
            case AEvent.AEVENT_LIVE_REMOVE_UPLOADER:
                Message msg2 = new Message();
                msg2.what = 1;
                Bundle b2 = new Bundle();
                b2.putString("data",((JSONObject)eventObj).toString());
                msg2.setData(b2);
                uiHandler.sendMessage(msg2);
                break;
            case AEvent.AEVENT_LIVE_STOP_OK:
                uiHandler.sendEmptyMessage(3);
                break;
            case AEvent.AEVENT_LIVE_RESIZE_ALL_VIDEO:
                Message msg4 = new Message();
                msg4.what = 4;
                Bundle b4 = new Bundle();
                b4.putByteArray("data", (byte[]) eventObj);
                msg4.setData(b4);
                uiHandler.sendMessage(msg4);
                break;
            case AEvent.AEVENT_LIVE_ERROR:
                Message msg5 = new Message();
                msg5.what = 5;
                Bundle b5 = new Bundle();
                b5.putString("data", (String) eventObj);
                msg5.setData(b5);
                uiHandler.sendMessage(msg5);
                break;
        }


        String[] datas;
        switch (aEventID){
            case AEvent.AEVENT_CHATROOM_CREATE_OK:
            case AEvent.AEVENT_CHATROOM_JOIN_OK:
                datas = eventObj.toString().split(":");
                chatroomId = datas[0];
                maxMessageLength = Integer.parseInt(datas[1]);
                joinChatroomOk = true;
                reportCP();
                break;
            case AEvent.AEVENT_CHATROOM_CREATE_FAILED:
            case AEvent.AEVENT_CHATROOM_JOIN_FAILED:
                final String err = eventObj.toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MLOC.showMsg(VideoLiveActivity.this,err.toString());
                    }
                });
                finish();
                break;
            case AEvent.AEVENT_CHATROOM_REV_MSG:
                StarIMMessage revMsg = (StarIMMessage) eventObj;
                mDatas.add(revMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case AEvent.AEVENT_CHATROOM_REV_PRIVATE_MSG:
                StarIMMessage revMsgPrivate = (StarIMMessage) eventObj;
                mDatas.add(revMsgPrivate);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case AEvent.AEVENT_CHATROOM_GET_ONLINE_NUMBER:
                onLineUserNumber = (int) eventObj;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        vOnlineNum.setText(""+onLineUserNumber);
//                    }
//                });
                break;
            case AEvent.AEVENT_CHATROOM_ERROR:
                final String err2 = eventObj.toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(err2.equals("CHATROOM_ERRID_ROOMID_ONLINE_OUTOFLIMIT")){
                            MLOC.showMsg(VideoLiveActivity.this,"超出人数上限");
                        }else{
                            MLOC.showMsg(VideoLiveActivity.this,err2.toString());
                        }
                    }
                });
                finish();
                break;
            case AEvent.AEVENT_CHATROOM_SELF_KICKED:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MLOC.showMsg(VideoLiveActivity.this,"你已被踢出");
                        VideoLiveActivity.this.finish();
                    }
                });
                break;
            case AEvent.AEVENT_CHATROOM_SELF_BANNED:
                final String banTime = eventObj.toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MLOC.showMsg(VideoLiveActivity.this,"你已被禁言,"+banTime+"秒后自动解除");
                    }
                });
                break;
            case AEvent.AEVENT_CHATROOM_KICK_OUT_OK:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MLOC.showMsg(VideoLiveActivity.this,"踢人成功");
                    }
                });
                break;
            case AEvent.AEVENT_CHATROOM_KICK_OUT_FAILED:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MLOC.showMsg(VideoLiveActivity.this,"踢人失败");
                    }
                });
                break;
            case AEvent.AEVENT_CHATROOM_BAN_USER_OK:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MLOC.showMsg(VideoLiveActivity.this,"禁言成功");
                    }
                });
                break;
            case AEvent.AEVENT_CHATROOM_BAN_USER_FAILED:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MLOC.showMsg(VideoLiveActivity.this,"禁言失败");
                    }
                });
                break;
            case AEvent.AEVENT_CHATROOM_STOP_OK:
                VideoLiveActivity.this.finish();
                break;
            case AEvent.AEVENT_CHATROOM_DELETE_OK:
                VideoLiveActivity.this.finish();
                break;
            case AEvent.AEVENT_CHATROOM_SEND_MSG_SUCCESS:
                break;
            case AEvent.AEVENT_CHATROOM_SEND_MSG_FAILED:
                break;
        }

    }

    private void reportCP(){
        if(joinLiveOk&&joinChatroomOk){
            if(MLOC.userId.equals(createrId)){
                InterfaceUrls.demoReportChatAndLive(channelId,chatroomId);
            }
        }
    }

    private Handler uiHandler = new UIHandler();
    class UIHandler extends Handler{
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    try {
                        JSONObject data = new JSONObject(msg.getData().getString("data"));
                        String addId = data.getString("userId");
                        int bigW = data.getInt("bigW");
                        int bigH = data.getInt("bigH");
                        int smallW = data.getInt("smallW");
                        int smallH = data.getInt("smallH");
                        int upId = data.getInt("upId");
                        ViewPosition vp = addNewPlayer();
                        if(vp!=null){
                            vp.setUserId(addId);
                            vp.setBigW(bigW);
                            vp.setBigH(bigH);
                            vp.setSmallW(smallW);
                            vp.setSmallH(smallH);
                            vp.setUpId(upId);
                            vp.getVideoPlayer().setZOrderMediaOverlay(true);
                            StarPlayer newVideoView = vp.getVideoPlayer();
                            StarManager.getInstance().meetingAddUser(newVideoView,addId,upId);
                            newVideoView.setScalType(smallW,smallH,
                                    StarPlayerScaleType.DRAW_TYPEDRAW_TYPE_CENTER_TOP
                            );
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    try {
                        JSONObject data = new JSONObject(msg.getData().getString("data"));
                        String removeUserId = data.getString("userId");
                        removeLeavePlayer(removeUserId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    for(ViewPosition viewPosition:mViewList){
                        viewPosition.getVideoPlayer().setScalType(
                                viewPosition.getSmallW(),
                                viewPosition.getSmallH(),
                                StarPlayerScaleType.DRAW_TYPEDRAW_TYPE_CENTER_TOP
                        );
                    }
                    break;
                case 3:
                    finish();
                    break;
                case 4:
                    byte[] config = msg.getData().getByteArray("data");
                    for(ViewPosition info:mViewList){
                        for(int i=0;i<config.length;i++){
                            if(info.getUpId()==i){
                                if(config[i]== StarLiveVideoSizeConfigType.LIVE_STREAM_CONFIG_BIG_BYTE){
                                    info.getVideoPlayer().setScalType(info.getBigW(),info.getBigH(), StarPlayerScaleType.DRAW_TYPEDRAW_TYPE_CENTER_TOP);
                                }else if(config[i]== StarLiveVideoSizeConfigType.LIVE_STREAM_CONFIG_SMALL_BYTE){
                                    info.getVideoPlayer().setScalType(info.getSmallW(),info.getSmallH(),StarPlayerScaleType.DRAW_TYPEDRAW_TYPE_CENTER_TOP);
                                }
                            }
                        }
                    }
                    break;
                case 5:
                    String errStr = msg.getData().getString("data");
                    MLOC.showMsg(getApplicationContext(),errStr);
                    onBackPressed();
            }
            super.handleMessage(msg);
        }
    };


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
                convertView = mInflater.inflate(R.layout.item_chatroom_msg_list,null);
                holder.vUserName = (TextView) convertView.findViewById(R.id.item_user_name);
                holder.vUserId = (TextView) convertView.findViewById(R.id.item_user_id);
                holder.vMsg = (TextView) convertView.findViewById(R.id.item_msg);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }

            holder.vUserId.setText(mDatas.get(position).fromId);
            holder.vMsg.setText(mDatas.get(position).contentData);

            return convertView;
        }
    }

    public class ViewHolder{
        public TextView vUserName;
        public TextView vUserId;
        public TextView vMsg;
    }


    private void showManagerDialog(final String userId) {
        if(!userId.equals(MLOC.userId)){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            if(createrId.equals(MLOC.userId)){
                final String[] Items={"踢出房间","禁止发言","私信"};
                builder.setItems(Items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            StarManager.getInstance().kickOutUser(userId);
                        }else if(i==1){
                            StarManager.getInstance().banToSendMessage(userId,60);
                        }else if(i==2){
                            mPrivateMsgTargetId = userId;
                            vEditText.setText("[私"+userId+"]");
                        }
                    }
                });
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

}
