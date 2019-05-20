package com.starrtc.demo.demo.videomeeting;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Set;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.demo.demo.MLOC;
import com.starrtc.demo.demo.im.group.MessageGroupSettingActivity;
import com.starrtc.demo.listener.XHMeetingManagerListener;
import com.starrtc.demo.serverAPI.InterfaceUrls;
import com.starrtc.demo.ui.CircularCoverView;
import com.starrtc.demo.utils.AEvent;
import com.starrtc.demo.utils.DensityUtils;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.api.XHConstants;
import com.starrtc.starrtcsdk.api.XHCustomConfig;
import com.starrtc.starrtcsdk.api.XHMeetingItem;
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback;
import com.starrtc.starrtcsdk.apiInterface.IXHMeetingManager;
import com.starrtc.starrtcsdk.core.StarRtcCore;
import com.starrtc.starrtcsdk.core.audio.StarRTCAudioManager;
import com.starrtc.starrtcsdk.core.player.StarPlayer;
import com.starrtc.starrtcsdk.core.player.StarPlayerScaleType;

public class VideoMeetingActivity extends BaseActivity{

    public static String MEETING_ID         = "MEETING_ID";          //会议ID
    public static String MEETING_NAME       = "CLASS_NAME";         //会议名称
    public static String MEETING_TYPE       = "CLASS_TYPE";         //会议类型
    public static String MEETING_CREATER       = "CLASS_CREATOR";   //会议创建者

    private String meetingId;
    private String meetingName;
    private String createrId;

    private TextView vMeetingName;

    private RelativeLayout vPlayerView;
    private ArrayList<ViewPosition> mPlayerList;
    private int borderW = 0;
    private int borderH = 0;

    private IXHMeetingManager meetingManager;
    private StarRTCAudioManager starRTCAudioManager;

    private Boolean isPortrait = true;
    private boolean rtmpPushing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_meeting);

        starRTCAudioManager = StarRTCAudioManager.create(getApplicationContext());
        starRTCAudioManager.start(new StarRTCAudioManager.AudioManagerEvents() {
            @Override
            public void onAudioDeviceChanged(StarRTCAudioManager.AudioDevice selectedAudioDevice, Set<StarRTCAudioManager.AudioDevice> availableAudioDevices) {

            }
        });

        DisplayMetrics dm = getResources().getDisplayMetrics();
        if(dm.heightPixels>dm.widthPixels){
            isPortrait = true;
        }else{
            isPortrait = false;
        }

        meetingManager = XHClient.getInstance().getMeetingManager(this);
        meetingManager.setRtcMediaType(XHConstants.XHRtcMediaTypeEnum.STAR_RTC_MEDIA_TYPE_VIDEO_AND_AUDIO);
        meetingManager.addListener(new XHMeetingManagerListener());

        addListener();

        meetingId = getIntent().getStringExtra(MEETING_ID);
        meetingName = getIntent().getStringExtra(MEETING_NAME);
        createrId = getIntent().getStringExtra(MEETING_CREATER);

        vMeetingName = (TextView) findViewById(R.id.live_id_text);
        vMeetingName.setText("会议："+meetingName);


        vPlayerView = (RelativeLayout) findViewById(R.id.view1);
        borderW = DensityUtils.screenWidth(this);
        borderH = DensityUtils.screenHeight(this);//-DensityUtils.dip2px(this,25);
        mPlayerList = new ArrayList<>();

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.push_rtmp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rtmpPushing){
                    stopPushRtmp();
                }else{
                    showAddDialog();
                }
            }
        });

        findViewById(R.id.switch_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meetingManager.switchCamera();
            }
        });
        init();
    }

    private void showAddDialog(){
        final Dialog dialog = new Dialog(this,R.style.dialog_popup);
        dialog.setContentView(R.layout.dialog_input_rtmp_url);
        ((EditText)dialog.findViewById(R.id.rtmpurl)).setText("rtmp://");
        ((EditText)dialog.findViewById(R.id.rtmpurl)).setText("rtmp://62.234.134.38/live/starrtc");
        Window win = dialog.getWindow();
        win.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        win.setGravity(Gravity.CENTER);
        dialog.setCanceledOnTouchOutside(true);
        dialog.findViewById(R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rtmpUrl = ((EditText)dialog.findViewById(R.id.rtmpurl)).getText().toString();
                if(TextUtils.isEmpty(rtmpUrl)){
                    MLOC.showMsg(VideoMeetingActivity.this,"推流地址不能为空");
                }else{
                    pushRtmp(rtmpUrl);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void pushRtmp(String url){
        meetingManager.pushRtmp(url, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                rtmpPushing = true;
                MLOC.showMsg(VideoMeetingActivity.this,"推流成功");
                ((TextView)findViewById(R.id.push_rtmp)).setText("停止");
            }

            @Override
            public void failed(final String errMsg) {
                rtmpPushing = false;
                MLOC.showMsg(VideoMeetingActivity.this,"推流失败"+errMsg);
                ((TextView)findViewById(R.id.push_rtmp)).setText("RTMP");
            }
        });
    }
    private void stopPushRtmp(){
        meetingManager.stopPushRtmp( new IXHResultCallback() {
            @Override
            public void success(Object data) {
                rtmpPushing = false;
                MLOC.showMsg(VideoMeetingActivity.this,"停止推流成功");
                ((TextView)findViewById(R.id.push_rtmp)).setText("RTMP");
            }

            @Override
            public void failed(final String errMsg) {
                MLOC.showMsg(VideoMeetingActivity.this,"停止推流失败"+errMsg);
            }
        });
    }

    private void init(){
        if(createrId.equals(MLOC.userId)){
            if(meetingId==null){
                createNewMeeting();
            }else {
                joinMeeting();
            }
        }else{
            if(meetingId==null){
                MLOC.showMsg(VideoMeetingActivity.this,"会议ID为空");
            }else {
                joinMeeting();
            }
        }
    }

    private void createNewMeeting(){
        //创建新直播
        final XHMeetingItem meetingItem = new XHMeetingItem();
        meetingItem.setMeetingName(meetingName);
        meetingItem.setMeetingType((XHConstants.XHMeetingType) getIntent().getSerializableExtra(MEETING_TYPE));
        meetingManager.createMeeting(meetingItem, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                meetingId = (String) data;
                if(MLOC.SERVER_TYPE.equals(MLOC.SERVER_TYPE_PUBLIC)){
                    InterfaceUrls.demoReportMeeting(meetingId,meetingName,createrId);
                }else{
                    try {
                        JSONObject info = new JSONObject();
                        info.put("id",meetingId);
                        info.put("creator",MLOC.userId);
                        info.put("name",meetingName);
                        String infostr = info.toString();
                        infostr = URLEncoder.encode(infostr,"utf-8");
                        meetingManager.saveToList(MLOC.userId,MLOC.CHATROOM_LIST_TYPE_MEETING,meetingId,infostr,null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }
                joinMeeting();
            }
            @Override
            public void failed(final String errMsg) {
                MLOC.showMsg(VideoMeetingActivity.this,errMsg);
                stopAndFinish();
            }
        });
    }

    private void stop(){
        meetingManager.leaveMeeting(meetingId, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                stopAndFinish();
            }

            @Override
            public void failed(final String errMsg) {
                MLOC.showMsg(VideoMeetingActivity.this,errMsg);
                stopAndFinish();
            }
        });
    }

    private void joinMeeting(){
        //开始直播
        meetingManager.joinMeeting(meetingId, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                MLOC.d("XHMeetingManager","startLive success "+data);
            }
            @Override
            public void failed(final String errMsg) {
                MLOC.d("XHMeetingManager","joinMeeting failed "+errMsg);
                MLOC.showMsg(VideoMeetingActivity.this,errMsg);
                stopAndFinish();
            }
        });
    }

    public void addListener(){
        AEvent.addListener(AEvent.AEVENT_MEETING_ADD_UPLOADER,this);
        AEvent.addListener(AEvent.AEVENT_MEETING_REMOVE_UPLOADER,this);
        AEvent.addListener(AEvent.AEVENT_MEETING_ERROR,this);
        AEvent.addListener(AEvent.AEVENT_MEETING_GET_ONLINE_NUMBER,this);
        AEvent.addListener(AEvent.AEVENT_MEETING_SELF_KICKED,this);
        AEvent.addListener(AEvent.AEVENT_MEETING_SELF_BANNED,this);
        AEvent.addListener(AEvent.AEVENT_MEETING_REV_MSG,this);
        AEvent.addListener(AEvent.AEVENT_MEETING_REV_PRIVATE_MSG,this);
    }
    public void removeListener(){
        AEvent.removeListener(AEvent.AEVENT_MEETING_ADD_UPLOADER,this);
        AEvent.removeListener(AEvent.AEVENT_MEETING_REMOVE_UPLOADER,this);
        AEvent.removeListener(AEvent.AEVENT_MEETING_ERROR,this);
        AEvent.removeListener(AEvent.AEVENT_MEETING_GET_ONLINE_NUMBER,this);
        AEvent.removeListener(AEvent.AEVENT_MEETING_SELF_KICKED,this);
        AEvent.removeListener(AEvent.AEVENT_MEETING_SELF_BANNED,this);
        AEvent.removeListener(AEvent.AEVENT_MEETING_REV_MSG,this);
        AEvent.removeListener(AEvent.AEVENT_MEETING_REV_PRIVATE_MSG,this);
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
        new AlertDialog.Builder(VideoMeetingActivity.this).setCancelable(true)
                .setTitle("是否退出会议?")
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
        ViewPosition newOne = new ViewPosition();
        newOne.setUserId(addUserID);
        StarPlayer player = new StarPlayer(this);
        newOne.setVideoPlayer(player);
        mPlayerList.add(newOne);
        vPlayerView.addView(player);
        CircularCoverView coverView = new CircularCoverView(this);
        coverView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        coverView.setCoverColor(Color.BLACK);
        coverView.setRadians(35, 35, 35, 35,10);
        player.addView(coverView);
        player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLayout(v);
            }
        });
        resetLayout();
        player.setZOrderMediaOverlay(true);

        if(mPlayerList.size()==1){
            meetingManager.attachPlayerView(addUserID,player,true);
        }else{
            meetingManager.attachPlayerView(addUserID,player,false);
        }
    }

    private boolean isRuning = false;
    private void changeLayout(View v){
        if(isRuning) return;
        if(v == mPlayerList.get(0).getVideoPlayer())return;
        ViewPosition clickPlayer = null;
        int clickIndex = 0;
        for (int i=0;i<mPlayerList.size();i++){
            if(mPlayerList.get(i).getVideoPlayer()==v){
                clickIndex = i;
                clickPlayer = mPlayerList.remove(i);
                meetingManager.changeToBig(clickPlayer.getUserId());
                break;
            }
        }
        final ViewPosition mainPlayer = mPlayerList.remove(0);
        meetingManager.changeToSmall(mainPlayer.getUserId());
        mPlayerList.remove(clickPlayer);
        mPlayerList.add(0, clickPlayer);
        mPlayerList.add(clickIndex,mainPlayer);

        final ViewPosition finalClickPlayer = clickPlayer;
        startAnimation(finalClickPlayer.getVideoPlayer(),mainPlayer.getVideoPlayer());
    }

    private void startAnimation(final StarPlayer clickPlayer, final StarPlayer mainPlayer){
        final float clickStartW = clickPlayer.getWidth();
        final float clickStartH = clickPlayer.getHeight();
        final float clickEndW = mainPlayer.getWidth();
        final float clickEndH = mainPlayer.getHeight();
        final float mainStartW = mainPlayer.getWidth();
        final float mainStartH = mainPlayer.getHeight();
        final float mainEndW = clickPlayer.getWidth();
        final float mainEndH = clickPlayer.getHeight();

        final float clickStartX = clickPlayer.getX();
        final float clickStartY = clickPlayer.getY();
        final float clickEndX = mainPlayer.getX();
        final float clickEndY = mainPlayer.getY();
        final float mainStartX = mainPlayer.getX();
        final float mainStartY = mainPlayer.getY();
        final float mainEndX = clickPlayer.getX();
        final float mainEndY = clickPlayer.getY();

        if(XHCustomConfig.getInstance().getOpenGLESEnable()){
            clickPlayer.setX(clickEndX);
            clickPlayer.setY(clickEndY);
            clickPlayer.getLayoutParams().width = (int) clickEndW;
            clickPlayer.getLayoutParams().height = (int)clickEndH;
            clickPlayer.requestLayout();

            mainPlayer.setX(mainEndX);
            mainPlayer.setY(mainEndY);
            mainPlayer.getLayoutParams().width = (int) mainEndW;
            mainPlayer.getLayoutParams().height = (int) mainEndH;
            mainPlayer.requestLayout();
        }else{

            final ValueAnimator valTotal  = ValueAnimator.ofFloat(0f,1f);
            valTotal.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    clickPlayer.setX(clickStartX + (Float) animation.getAnimatedValue()*(clickEndX - clickStartX));
                    clickPlayer.setY(clickStartY + (Float) animation.getAnimatedValue()*(clickEndY - clickStartY));
                    clickPlayer.getLayoutParams().width = (int) (clickStartW + (Float) animation.getAnimatedValue()*(clickEndW - clickStartW));
                    clickPlayer.getLayoutParams().height = (int) (clickStartH + (Float) animation.getAnimatedValue()*(clickEndH - clickStartH));
                    clickPlayer.requestLayout();

                    mainPlayer.setX(mainStartX + (Float) animation.getAnimatedValue()*(mainEndX - mainStartX));
                    mainPlayer.setY(mainStartY + (Float) animation.getAnimatedValue()*(mainEndY - mainStartY));
                    mainPlayer.getLayoutParams().width = (int) (mainStartW + (Float) animation.getAnimatedValue()*(mainEndW - mainStartW));
                    mainPlayer.getLayoutParams().height = (int) (mainStartH + (Float) animation.getAnimatedValue()*(mainEndH - mainStartH));
                    mainPlayer.requestLayout();
                }
            });

            valTotal.setDuration(300);
            valTotal.setInterpolator(new LinearInterpolator());
            valTotal.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    isRuning = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isRuning = false;
                    clickPlayer.setScalType(StarPlayerScaleType.DRAW_TYPE_CENTER);
                    mainPlayer.setScalType(StarPlayerScaleType.DRAW_TYPE_CENTER);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    isRuning = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            valTotal.start();
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
                    meetingManager.changeToBig(mPlayerList.get(0).getUserId());
                    break;
                }
            }
        }
    }

    private void resetLayout(){
        if(isPortrait){
            switch (mPlayerList.size()){
                case 1:{
                    StarPlayer player = mPlayerList.get(0).getVideoPlayer();
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW,borderH);
                    player.setLayoutParams(lp);
                    player.setY(0);
                    player.setX(0);
                    break;
                }
                case 2:
                case 3:
                case 4:{
                    for(int i = 0;i<mPlayerList.size();i++){
                        if(i==0){
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW,borderH-borderW/3);
                            player.setLayoutParams(lp);
                            player.setY(0);
                            player.setX(0);
                        }else{
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/3,borderW/3);
                            player.setLayoutParams(lp);
                            player.setY(borderH-borderW/3);
                            player.setX(borderW/3*(i-1));
                        }
                    }
                    break;
                }
                case 5:
                case 6:
                case 7:{

                    for(int i = 0;i<mPlayerList.size();i++){
                        if(i==0){
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW,borderH-borderW/3*2);
                            player.setLayoutParams(lp);
                            player.setY(0);
                            player.setX(0);
                        }else if(i<4){
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/3,borderW/3);
                            player.setLayoutParams(lp);
                            player.setX(borderW/3*(i-1));
                            player.setY(borderH-borderW/3*2);
                        }else {
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/3,borderW/3);
                            player.setLayoutParams(lp);
                            player.setX(borderW/3*(i-4));
                            player.setY(borderH-borderW/3);
                        }
                    }
                    break;
                }
            }

        }else{
            switch (mPlayerList.size()){
                case 1:{
                    StarPlayer player = mPlayerList.get(0).getVideoPlayer();
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW,borderH);
                    player.setLayoutParams(lp);
                    player.setY(0);
                    player.setX(0);
                    break;
                }
                case 2:
                case 3:
                case 4:
                {
                    for(int i = 0;i<mPlayerList.size();i++){
                        if(i==0){
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/4*3,borderH);
                            player.setLayoutParams(lp);
                            player.setY(0);
                            player.setX(0);
                        }else{
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/4,borderH/3);
                            player.setLayoutParams(lp);
                            player.setY((i-1)*borderH/3);
                            player.setX(borderW/4*3);
                        }
                    }
                    break;
                }
                case 5:
                case 6:
                case 7:{
                    for(int i = 0;i<mPlayerList.size();i++){
                        if(i==0){
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/4*2,borderH);
                            player.setLayoutParams(lp);
                            player.setY(0);
                            player.setX(0);
                        }else if(i>0&&i<3){
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/4,borderH/3);
                            player.setLayoutParams(lp);
                            player.setY((i-1)*borderH/3);
                            player.setX(borderW/4*2);
                        }else{
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/4,borderH/3);
                            player.setLayoutParams(lp);
                            player.setY((i-3)*borderH/3);
                            player.setX(borderW/4*3);
                        }
                    }
                    break;
                }
            }
        }
    }
    @Override
    public void dispatchEvent(String aEventID, boolean success, final Object eventObj) {
        super.dispatchEvent(aEventID,success,eventObj);
        switch (aEventID){
            case AEvent.AEVENT_MEETING_ADD_UPLOADER:
                try {
                    JSONObject data = (JSONObject) eventObj;
                    String addId = data.getString("userID");
                    addPlayer(addId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case AEvent.AEVENT_MEETING_REMOVE_UPLOADER:
                try {
                    JSONObject data = (JSONObject) eventObj;
                    String removeUserId = data.getString("userID");
                    deletePlayer(removeUserId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case AEvent.AEVENT_MEETING_ERROR:
                String errStr = (String) eventObj;
                MLOC.showMsg(getApplicationContext(),errStr);
                stopAndFinish();
                break;
            case AEvent.AEVENT_MEETING_GET_ONLINE_NUMBER:
                break;
            case AEvent.AEVENT_MEETING_SELF_KICKED:
                MLOC.showMsg(VideoMeetingActivity.this,"你已被踢出");
                stopAndFinish();
                break;
            case AEvent.AEVENT_MEETING_SELF_BANNED:
                break;
            case AEvent.AEVENT_MEETING_REV_MSG:
                break;
            case AEvent.AEVENT_MEETING_REV_PRIVATE_MSG:
                break;
        }
    }
    private void stopAndFinish(){
        if(starRTCAudioManager!=null){
            starRTCAudioManager.stop();
        }
        removeListener();
        finish();
    }
}
