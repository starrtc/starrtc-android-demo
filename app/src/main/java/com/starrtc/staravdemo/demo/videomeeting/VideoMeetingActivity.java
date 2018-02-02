package com.starrtc.staravdemo.demo.videomeeting;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.starrtc.staravdemo.R;
import com.starrtc.staravdemo.demo.MLOC;
import com.starrtc.staravdemo.utils.AEvent;
import com.starrtc.staravdemo.utils.IEventListener;
import com.starrtc.starrtcsdk.live.StarLiveConfig;
import com.starrtc.starrtcsdk.live.StarLiveVideoSizeConfigType;
import com.starrtc.starrtcsdk.player.StarPlayer;
import com.starrtc.starrtcsdk.player.StarPlayerScaleType;
import com.starrtc.starrtcsdk.StarManager;
import com.starrtc.starrtcsdk.utils.StarLog;
import com.starrtc.starrtcsdk.utils.StringUtils;

public class VideoMeetingActivity extends Activity implements IEventListener {

    private LinearLayout vLine1,vLine2;
    private List<ViewPosition> mViewList;

    public static String MEETING_ID         = "LIVE_ID";          //会议ID
    public static String CHANNEL_ID         = "CHANNEL_ID";       //频道ID

    private TextView vRoomId;
    private String meetingId;
    private String channelId;

    private int bigUpid;
    private ViewPosition bigViewPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video_meeting);

        meetingId = getIntent().getStringExtra(MEETING_ID);
        channelId = getIntent().getStringExtra(CHANNEL_ID);

        if(StringUtils.isEmpty(meetingId)){
            meetingId = "meeting_"+ meetingId;
        }

        vRoomId = (TextView) findViewById(R.id.live_id_text);
        vRoomId.setText("会议编号："+meetingId);

        vLine1 = (LinearLayout) findViewById(R.id.line1);
        vLine2 = (LinearLayout) findViewById(R.id.line2);

        bigViewPosition = addNewPlayer();
        bigViewPosition.setUserId(MLOC.agentId+"_"+MLOC.userId);
        bigViewPosition.setUpId(-1);
        bigViewPosition.setBigW(368);
        bigViewPosition.setBigH(640);
        bigViewPosition.setSmallW(368);
        bigViewPosition.setSmallH(640);
        bigUpid = -1;
        StarManager.getInstance().initLive(getApplicationContext(), bigViewPosition.getVideoPlayer());
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
        super.onStop();
    }

    @Override
    public void onBackPressed(){
        StarManager.getInstance().stopLive();
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
                    StarManager.getInstance().setPreviewVideoView(1,bigViewPosition.getVideoPlayer());
                    if(StringUtils.isNotEmpty(channelId)){
                        StarLog.d("","使用已存在频道上传视频："+channelId);
                        StarManager.getInstance().applyStartMeeting( new StarLiveConfig(),meetingId,channelId);
                    }else{
                        StarLog.d("","申请新的会议频道上传视频");
                        StarManager.getInstance().applyStartNewMeeting(new StarLiveConfig(),meetingId,7);
                    }
                }else{
                    MLOC.showMsg("initEncoder ERROR!!!");
                    finish();
                }
                break;
            case AEvent.AEVENT_LIVE_JOIN_OK:

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
    }

    private Handler uiHandler = new UIHandler();
    class UIHandler extends Handler{
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    try {
                        StarLog.d("@@@@","add new player:"+msg.getData().getString("data"));
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

                        if(viewPosition.getUpId()==bigUpid){
                            StarLog.d("@@@@","setScalType upid:"+viewPosition.getUpId()+"|"+viewPosition.getBigW()+"|"+viewPosition.getBigH() );
                            viewPosition.getVideoPlayer().setScalType(
                                    viewPosition.getBigW(),
                                    viewPosition.getBigH(),
                                    StarPlayerScaleType.DRAW_TYPEDRAW_TYPE_CENTER_TOP
                            );
                        }else{
                            StarLog.d("@@@@","setScalType upid:"+viewPosition.getUpId()+"|"+viewPosition.getSmallW()+"|"+viewPosition.getSmallH() );
                            viewPosition.getVideoPlayer().setScalType(
                                    viewPosition.getSmallW(),
                                    viewPosition.getSmallH(),
                                    StarPlayerScaleType.DRAW_TYPEDRAW_TYPE_CENTER_TOP
                            );
                        }

                    }
                    StarLog.d("@@@@","setScalType ===============" );
                    break;
                case 3:
                    finish();
                    break;
                case 4:

                    byte[] config = msg.getData().getByteArray("data");
                    StarLog.d("@@@@","resetAll " );
                    for(ViewPosition info:mViewList){
                        for(int i=0;i<config.length;i++){
                            if(info.getUpId()==i){
                                if(config[i]== StarLiveVideoSizeConfigType.LIVE_STREAM_CONFIG_BIG_BYTE){
                                    StarLog.d("@@@@","setScalType upid:"+info.getUpId()+"|"+info.getBigW()+"|"+info.getBigH() );
                                    info.getVideoPlayer().setScalType(info.getBigW(),info.getBigH(), StarPlayerScaleType.DRAW_TYPEDRAW_TYPE_CENTER_TOP);
                                }else if(config[i]== StarLiveVideoSizeConfigType.LIVE_STREAM_CONFIG_SMALL_BYTE){
                                    StarLog.d("@@@@","setScalType upid:"+info.getUpId()+"|"+info.getSmallW()+"|"+info.getSmallH() );
                                    info.getVideoPlayer().setScalType(info.getSmallW(),info.getSmallH(),StarPlayerScaleType.DRAW_TYPEDRAW_TYPE_CENTER_TOP);
                                }
                            }
                        }
                    }
                    StarLog.d("@@@@","resetAll=================== " );
                    break;
                case 5:
                    String errStr = msg.getData().getString("data");
                    MLOC.showMsg(getApplicationContext(),errStr);
                    onBackPressed();
            }
            super.handleMessage(msg);
        }
    };


}
